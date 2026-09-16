package de.franconia.tuebingen.adh.privacy;

import de.franconia.tuebingen.adh.corps.CorpsMembershipRepository;
import de.franconia.tuebingen.adh.corps.MembershipStatus;

import de.franconia.tuebingen.adh.privacy.dto.PrivacyRuleResponse;
import de.franconia.tuebingen.adh.privacy.dto.UpdatePrivacyRuleRequest;

import de.franconia.tuebingen.adh.profile.AddressRepository;
import de.franconia.tuebingen.adh.profile.ProfileRepository;

import de.franconia.tuebingen.adh.user.User;
import de.franconia.tuebingen.adh.user.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class PrivacyService {

    private final PrivacyRuleRepository
            privacyRuleRepository;

    private final UserRepository
            userRepository;

    private final ProfileRepository
            profileRepository;

    private final AddressRepository
            addressRepository;

    private final CorpsMembershipRepository
            membershipRepository;

    public PrivacyService(
            PrivacyRuleRepository privacyRuleRepository,
            UserRepository userRepository,
            ProfileRepository profileRepository,
            AddressRepository addressRepository,
            CorpsMembershipRepository membershipRepository
    ) {
        this.privacyRuleRepository =
                privacyRuleRepository;

        this.userRepository =
                userRepository;

        this.profileRepository =
                profileRepository;

        this.addressRepository =
                addressRepository;

        this.membershipRepository =
                membershipRepository;
    }

    @Transactional(readOnly = true)
    public List<PrivacyRuleResponse> getOwnRules(
            UUID ownerUserId
    ) {

        return privacyRuleRepository
                .findByOwnerId(ownerUserId)
                .stream()
                .map(this::toResponse)
                .sorted(
                        Comparator
                                .comparing(
                                        (PrivacyRuleResponse response) ->
                                                response
                                                        .resourceType()
                                                        .name()
                                )
                                .thenComparing(
                                        response ->
                                                response
                                                        .resourceId()
                                                        .toString()
                                )
                                .thenComparing(
                                        response ->
                                                response
                                                        .field()
                                                        .name()
                                )
                )
                .toList();
    }

    @Transactional
    public PrivacyRuleResponse updateRule(
            UUID ownerUserId,
            UpdatePrivacyRuleRequest request
    ) {

        validateFieldForResource(
                request.resourceType(),
                request.field()
        );

        validateOwnedResource(
                ownerUserId,
                request.resourceType(),
                request.resourceId()
        );

        Set<MembershipStatus> allowedStatuses =
                new HashSet<>(
                        request.allowedStatuses()
                );

        Set<UUID> allowedUserIds =
                new HashSet<>(
                        request.allowedUserIds()
                );

        if (
                allowedStatuses.isEmpty()
                && allowedUserIds.isEmpty()
        ) {

            privacyRuleRepository
                    .findRule(
                            ownerUserId,
                            request.resourceType(),
                            request.resourceId(),
                            request.field()
                    )
                    .ifPresent(
                            privacyRuleRepository::delete
                    );

            return publicResponse(request);
        }

        List<User> allowedUsers =
                userRepository
                        .findAllById(
                                allowedUserIds
                        );

        boolean allUsersValid =
                allowedUsers.size()
                        == allowedUserIds.size()
                && allowedUsers
                        .stream()
                        .allMatch(
                                User::isEnabled
                        );

        if (!allUsersValid) {
            throw new InvalidPrivacyRuleException(
                    "Freigegebene Nutzer müssen existieren und aktiviert sein"
            );
        }

        PrivacyRule rule =
                privacyRuleRepository
                        .findRule(
                                ownerUserId,
                                request.resourceType(),
                                request.resourceId(),
                                request.field()
                        )
                        .orElseGet(() -> {

                            User owner =
                                    userRepository
                                            .findById(
                                                    ownerUserId
                                            )
                                            .orElseThrow(
                                                    PrivacyResourceNotFoundException::new
                                            );

                            PrivacyRule created =
                                    new PrivacyRule();

                            created.setOwner(owner);

                            created.setResourceType(
                                    request.resourceType()
                            );

                            created.setResourceId(
                                    request.resourceId()
                            );

                            created.setField(
                                    request.field()
                            );

                            return created;
                        });

        rule.getAllowedStatuses().clear();
        rule.getAllowedStatuses().addAll(
                allowedStatuses
        );

        rule.getAllowedUsers().clear();
        rule.getAllowedUsers().addAll(
                allowedUsers
        );

        PrivacyRule saved =
                privacyRuleRepository.save(rule);

        return toResponse(saved);
    }

    @Transactional
    public void deleteRule(
            UUID ownerUserId,
            PrivacyResourceType resourceType,
            UUID resourceId,
            PrivacyField field
    ) {

        validateFieldForResource(
                resourceType,
                field
        );

        validateOwnedResource(
                ownerUserId,
                resourceType,
                resourceId
        );

        privacyRuleRepository
                .findRule(
                        ownerUserId,
                        resourceType,
                        resourceId,
                        field
                )
                .ifPresent(
                        privacyRuleRepository::delete
                );
    }

    @Transactional
    public void deleteRulesForResource(
            UUID ownerUserId,
            PrivacyResourceType resourceType,
            UUID resourceId
    ) {

        privacyRuleRepository
                .deleteForResource(
                        ownerUserId,
                        resourceType,
                        resourceId
                );
    }

    @Transactional(readOnly = true)
    public PrivacyAccessContext createAccessContext(
            UUID viewerUserId,
            UUID ownerUserId
    ) {

        return createAccessContexts(
                viewerUserId,
                Set.of(ownerUserId)
        ).get(ownerUserId);
    }

    @Transactional(readOnly = true)
    public Map<UUID, PrivacyAccessContext>
    createAccessContexts(
            UUID viewerUserId,
            Collection<UUID> ownerUserIds
    ) {

        Set<UUID> owners =
                new HashSet<>(
                        ownerUserIds
                );

        if (owners.isEmpty()) {
            return Map.of();
        }

        Set<MembershipStatus> viewerStatuses =
                new HashSet<>(
                        membershipRepository
                                .findMembershipStatusesByUserId(
                                        viewerUserId
                                )
                );

        List<PrivacyRule> rules =
                privacyRuleRepository
                        .findByOwnerIds(owners);

        Map<UUID, List<PrivacyRule>>
                rulesByOwner =
                new HashMap<>();

        for (PrivacyRule rule : rules) {

            rulesByOwner
                    .computeIfAbsent(
                            rule
                                    .getOwner()
                                    .getId(),
                            ignored ->
                                    new ArrayList<>()
                    )
                    .add(rule);
        }

        Map<UUID, PrivacyAccessContext>
                contexts =
                new HashMap<>();

        for (UUID ownerUserId : owners) {

            contexts.put(
                    ownerUserId,
                    new PrivacyAccessContext(
                            viewerUserId,
                            ownerUserId,
                            viewerStatuses,
                            rulesByOwner
                                    .getOrDefault(
                                            ownerUserId,
                                            List.of()
                                    )
                    )
            );
        }

        return Map.copyOf(contexts);
    }

    private void validateFieldForResource(
            PrivacyResourceType resourceType,
            PrivacyField field
    ) {

        if (!field.supports(resourceType)) {
            throw new InvalidPrivacyRuleException(
                    "Privacy-Feld passt nicht zum Ressourcentyp"
            );
        }
    }

    private void validateOwnedResource(
            UUID ownerUserId,
            PrivacyResourceType resourceType,
            UUID resourceId
    ) {

        boolean owned =
                switch (resourceType) {

                    case PROFILE ->
                            profileRepository
                                    .findByUserId(
                                            ownerUserId
                                    )
                                    .map(profile ->
                                            profile
                                                    .getId()
                                                    .equals(
                                                            resourceId
                                                    )
                                    )
                                    .orElse(false);

                    case ADDRESS ->
                            addressRepository
                                    .findByIdAndUserId(
                                            resourceId,
                                            ownerUserId
                                    )
                                    .isPresent();

                    case CORPS_MEMBERSHIP ->
                            membershipRepository
                                    .findByIdAndUserId(
                                            resourceId,
                                            ownerUserId
                                    )
                                    .isPresent();
                };

        if (!owned) {
            throw new PrivacyResourceNotFoundException();
        }
    }

    private PrivacyRuleResponse publicResponse(
            UpdatePrivacyRuleRequest request
    ) {

        return new PrivacyRuleResponse(
                request.resourceType(),
                request.resourceId(),
                request.field(),
                List.of(),
                List.of()
        );
    }

    private PrivacyRuleResponse toResponse(
            PrivacyRule rule
    ) {

        List<MembershipStatus> statuses =
                rule
                        .getAllowedStatuses()
                        .stream()
                        .sorted(
                                Comparator.comparing(
                                        MembershipStatus::name
                                )
                        )
                        .toList();

        List<UUID> userIds =
                rule
                        .getAllowedUsers()
                        .stream()
                        .map(User::getId)
                        .sorted(
                                Comparator.comparing(
                                        UUID::toString
                                )
                        )
                        .toList();

        return new PrivacyRuleResponse(
                rule.getResourceType(),
                rule.getResourceId(),
                rule.getField(),
                statuses,
                userIds
        );
    }
}