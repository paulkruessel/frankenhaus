package de.franconia.tuebingen.adh.privacy;

import de.franconia.tuebingen.adh.corps.MembershipStatus;
import de.franconia.tuebingen.adh.user.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class PrivacyAccessContext {

    private final UUID viewerUserId;
    private final UUID ownerUserId;

    private final Set<MembershipStatus>
            viewerStatuses;

    private final Map<RuleKey, Grant>
            grants;

    PrivacyAccessContext(
            UUID viewerUserId,
            UUID ownerUserId,
            Set<MembershipStatus> viewerStatuses,
            Collection<PrivacyRule> rules
    ) {
        this.viewerUserId = viewerUserId;
        this.ownerUserId = ownerUserId;
        this.viewerStatuses =
                Set.copyOf(viewerStatuses);

        Map<RuleKey, Grant> grantMap =
                new HashMap<>();

        for (PrivacyRule rule : rules) {

            Set<UUID> allowedUserIds =
                    rule.getAllowedUsers()
                            .stream()
                            .map(User::getId)
                            .collect(
                                    Collectors.toUnmodifiableSet()
                            );

            Set<MembershipStatus> allowedStatuses =
                    Set.copyOf(
                            rule.getAllowedStatuses()
                    );

            grantMap.put(
                    new RuleKey(
                            rule.getResourceType(),
                            rule.getResourceId(),
                            rule.getField()
                    ),
                    new Grant(
                            allowedStatuses,
                            allowedUserIds
                    )
            );
        }

        this.grants =
                Map.copyOf(grantMap);
    }

    public boolean canView(
            PrivacyResourceType resourceType,
            UUID resourceId,
            PrivacyField field
    ) {

        if (!field.supports(resourceType)) {
            throw new IllegalArgumentException(
                    "Privacy-Feld passt nicht zum Ressourcentyp"
            );
        }

        if (viewerUserId.equals(ownerUserId)) {
            return true;
        }

        Grant grant =
                grants.get(
                        new RuleKey(
                                resourceType,
                                resourceId,
                                field
                        )
                );

        if (grant == null) {
            return true;
        }

        if (
                grant.allowedUserIds()
                        .contains(viewerUserId)
        ) {
            return true;
        }

        return viewerStatuses
                .stream()
                .anyMatch(
                        grant.allowedStatuses()
                                ::contains
                );
    }

    private record RuleKey(
            PrivacyResourceType resourceType,
            UUID resourceId,
            PrivacyField field
    ) {
    }

    private record Grant(
            Set<MembershipStatus> allowedStatuses,
            Set<UUID> allowedUserIds
    ) {
    }
}