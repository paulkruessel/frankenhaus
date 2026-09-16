package de.franconia.tuebingen.adh.member;

import de.franconia.tuebingen.adh.common.dto.PageResponse;

import de.franconia.tuebingen.adh.corps.CorpsMembership;
import de.franconia.tuebingen.adh.corps.CorpsMembershipRepository;
import de.franconia.tuebingen.adh.corps.dto.CorpsMembershipResponse;

import de.franconia.tuebingen.adh.member.dto.MemberDetailResponse;
import de.franconia.tuebingen.adh.member.dto.MemberSummaryResponse;

import de.franconia.tuebingen.adh.privacy.PrivacyAccessContext;
import de.franconia.tuebingen.adh.privacy.PrivacyField;
import de.franconia.tuebingen.adh.privacy.PrivacyResourceType;
import de.franconia.tuebingen.adh.privacy.PrivacyService;

import de.franconia.tuebingen.adh.profile.Address;
import de.franconia.tuebingen.adh.profile.AddressRepository;
import de.franconia.tuebingen.adh.profile.PersonProfile;
import de.franconia.tuebingen.adh.profile.ProfileRepository;

import de.franconia.tuebingen.adh.profile.dto.AddressResponse;
import de.franconia.tuebingen.adh.profile.dto.ProfileResponse;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MemberService {

    private static final Comparator<String>
            DISPLAY_TEXT_COMPARATOR =
            Comparator.nullsLast(
                    String.CASE_INSENSITIVE_ORDER
            );

    private final ProfileRepository profileRepository;
    private final AddressRepository addressRepository;
    private final CorpsMembershipRepository membershipRepository;
    private final PrivacyService privacyService;

    public MemberService(
            ProfileRepository profileRepository,
            AddressRepository addressRepository,
            CorpsMembershipRepository membershipRepository,
            PrivacyService privacyService
    ) {
        this.profileRepository = profileRepository;
        this.addressRepository = addressRepository;
        this.membershipRepository = membershipRepository;
        this.privacyService = privacyService;
    }

    @Transactional(readOnly = true)
    public PageResponse<MemberSummaryResponse> searchMembers(
            UUID viewerUserId,
            String query,
            int page,
            int size
    ) {

        String normalizedQuery =
                normalizeQuery(query);

        List<PersonProfile> profiles =
                profileRepository
                        .findAllEnabledMembers();

        Set<UUID> ownerUserIds =
                profiles
                        .stream()
                        .map(profile ->
                                profile
                                        .getUser()
                                        .getId()
                        )
                        .collect(
                                Collectors.toSet()
                        );

        Map<UUID, PrivacyAccessContext>
                privacyContexts =
                privacyService
                        .createAccessContexts(
                                viewerUserId,
                                ownerUserIds
                        );

        Map<UUID, List<CorpsMembership>>
                membershipsByUser =
                normalizedQuery.isEmpty()
                        ? Map.of()
                        : loadMembershipsByUser(
                                ownerUserIds
                        );

        List<MemberSummaryResponse> matches =
                profiles
                        .stream()
                        .filter(profile ->
                                matchesSearch(
                                        profile,
                                        membershipsByUser,
                                        privacyContexts,
                                        normalizedQuery
                                )
                        )
                        .map(profile -> {

                            UUID ownerUserId =
                                    profile
                                            .getUser()
                                            .getId();

                            return toSummaryResponse(
                                    profile,
                                    privacyContexts
                                            .get(
                                                    ownerUserId
                                            )
                            );
                        })
                        .sorted(
                                Comparator
                                        .comparing(
                                                MemberSummaryResponse::lastName,
                                                DISPLAY_TEXT_COMPARATOR
                                        )
                                        .thenComparing(
                                                MemberSummaryResponse::firstName,
                                                DISPLAY_TEXT_COMPARATOR
                                        )
                                        .thenComparing(
                                                MemberSummaryResponse::id
                                        )
                        )
                        .toList();

        return page(
                matches,
                page,
                size
        );
    }

    @Transactional(readOnly = true)
    public MemberDetailResponse getMember(
            UUID viewerUserId,
            UUID targetUserId
    ) {

        PersonProfile profile =
                profileRepository
                        .findEnabledMemberByUserId(
                                targetUserId
                        )
                        .orElseThrow(
                                MemberNotFoundException::new
                        );

        PrivacyAccessContext privacyContext =
                privacyService
                        .createAccessContext(
                                viewerUserId,
                                targetUserId
                        );

        List<AddressResponse> addresses =
                addressRepository
                        .findByUserId(
                                targetUserId
                        )
                        .stream()
                        .map(address ->
                                toAddressResponse(
                                        address,
                                        privacyContext
                                )
                        )
                        .toList();

        List<CorpsMembershipResponse> memberships =
                membershipRepository
                        .findByUserId(
                                targetUserId
                        )
                        .stream()
                        .map(membership ->
                                toMembershipResponse(
                                        membership,
                                        privacyContext
                                )
                        )
                        .toList();

        return new MemberDetailResponse(
                toProfileResponse(
                        profile,
                        privacyContext
                ),
                addresses,
                memberships
        );
    }

    private String normalizeQuery(
            String query
    ) {

        return query == null
                ? ""
                : query
                        .trim()
                        .toLowerCase(
                                Locale.ROOT
                        );
    }

    private Map<UUID, List<CorpsMembership>>
    loadMembershipsByUser(
            Set<UUID> userIds
    ) {

        if (userIds.isEmpty()) {
            return Map.of();
        }

        return membershipRepository
                .findByUserIdsWithCorps(
                        userIds
                )
                .stream()
                .collect(
                        Collectors.groupingBy(
                                membership ->
                                        membership
                                                .getUser()
                                                .getId()
                        )
                );
    }

    private boolean matchesSearch(
            PersonProfile profile,
            Map<UUID, List<CorpsMembership>>
                    membershipsByUser,
            Map<UUID, PrivacyAccessContext>
                    privacyContexts,
            String query
    ) {

        if (query.isEmpty()) {
            return true;
        }

        UUID ownerUserId =
                profile
                        .getUser()
                        .getId();

        PrivacyAccessContext privacyContext =
                privacyContexts.get(
                        ownerUserId
                );

        if (
                matchesProfileField(
                        privacyContext,
                        profile,
                        PrivacyField.FIRST_NAME,
                        profile.getFirstName(),
                        query
                )
                || matchesProfileField(
                        privacyContext,
                        profile,
                        PrivacyField.LAST_NAME,
                        profile.getLastName(),
                        query
                )
                || matchesProfileField(
                        privacyContext,
                        profile,
                        PrivacyField.EMAIL,
                        profile.getUser().getEmail(),
                        query
                )
                || matchesProfileField(
                        privacyContext,
                        profile,
                        PrivacyField.ACADEMIC_DEGREE,
                        profile.getAcademicDegree(),
                        query
                )
                || matchesProfileField(
                        privacyContext,
                        profile,
                        PrivacyField.FIELD_OF_STUDY,
                        profile.getFieldOfStudy(),
                        query
                )
                || matchesProfileField(
                        privacyContext,
                        profile,
                        PrivacyField.JOB_TITLE,
                        profile.getJobTitle(),
                        query
                )
                || matchesProfileField(
                        privacyContext,
                        profile,
                        PrivacyField.COMPANY,
                        profile.getCompany(),
                        query
                )
                || matchesProfileField(
                        privacyContext,
                        profile,
                        PrivacyField.POSITION,
                        profile.getPosition(),
                        query
                )
                || matchesProfileField(
                        privacyContext,
                        profile,
                        PrivacyField.BIRTH_PLACE,
                        profile.getBirthPlace(),
                        query
                )
        ) {
            return true;
        }

        return membershipsByUser
                .getOrDefault(
                        ownerUserId,
                        List.of()
                )
                .stream()
                .anyMatch(membership ->
                        matchesMembershipField(
                                privacyContext,
                                membership,
                                PrivacyField.CORPS_NAME,
                                membership
                                        .getCorps()
                                        .getName(),
                                query
                        )
                        || matchesMembershipField(
                                privacyContext,
                                membership,
                                PrivacyField.NAME_IN_CORPS,
                                membership
                                        .getNameInCorps(),
                                query
                        )
                        || matchesMembershipField(
                                privacyContext,
                                membership,
                                PrivacyField.CORPS_LIST_NUMBER,
                                membership
                                        .getCorpsListNumber(),
                                query
                        )
                );
    }

    private boolean matchesProfileField(
            PrivacyAccessContext privacyContext,
            PersonProfile profile,
            PrivacyField field,
            String value,
            String query
    ) {

        return canViewProfileField(
                privacyContext,
                profile,
                field
        )
                && containsIgnoreCase(
                        value,
                        query
                );
    }

    private boolean matchesMembershipField(
            PrivacyAccessContext privacyContext,
            CorpsMembership membership,
            PrivacyField field,
            String value,
            String query
    ) {

        return canViewMembershipField(
                privacyContext,
                membership,
                field
        )
                && containsIgnoreCase(
                        value,
                        query
                );
    }

    private boolean containsIgnoreCase(
            String value,
            String query
    ) {

        return value != null
                && value
                        .toLowerCase(
                                Locale.ROOT
                        )
                        .contains(query);
    }

    private PageResponse<MemberSummaryResponse> page(
            List<MemberSummaryResponse> matches,
            int page,
            int size
    ) {

        long offset =
                (long) page * size;

        int fromIndex =
                (int) Math.min(
                        offset,
                        matches.size()
                );

        int toIndex =
                Math.min(
                        fromIndex + size,
                        matches.size()
                );

        PageImpl<MemberSummaryResponse> result =
                new PageImpl<>(
                        matches.subList(
                                fromIndex,
                                toIndex
                        ),
                        PageRequest.of(
                                page,
                                size
                        ),
                        matches.size()
                );

        return PageResponse.from(result);
    }

    private MemberSummaryResponse toSummaryResponse(
            PersonProfile profile,
            PrivacyAccessContext privacyContext
    ) {

        return new MemberSummaryResponse(
                profile.getUser().getId(),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.TITLE,
                        profile.getTitle()
                ),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.FIRST_NAME,
                        profile.getFirstName()
                ),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.LAST_NAME,
                        profile.getLastName()
                ),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.EMAIL,
                        profile
                                .getUser()
                                .getEmail()
                ),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.ACADEMIC_DEGREE,
                        profile.getAcademicDegree()
                ),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.JOB_TITLE,
                        profile.getJobTitle()
                ),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.COMPANY,
                        profile.getCompany()
                ),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.POSITION,
                        profile.getPosition()
                )
        );
    }

    private ProfileResponse toProfileResponse(
            PersonProfile profile,
            PrivacyAccessContext privacyContext
    ) {

        return new ProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.EMAIL,
                        profile
                                .getUser()
                                .getEmail()
                ),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.TITLE,
                        profile.getTitle()
                ),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.FIRST_NAME,
                        profile.getFirstName()
                ),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.LAST_NAME,
                        profile.getLastName()
                ),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.MOBILE_PHONE,
                        profile.getMobilePhone()
                ),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.BIRTH_DATE,
                        profile.getBirthDate()
                ),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.BIRTH_PLACE,
                        profile.getBirthPlace()
                ),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.WIKIPEDIA_URL,
                        profile.getWikipediaUrl()
                ),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.LINKEDIN_URL,
                        profile.getLinkedinUrl()
                ),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.XING_URL,
                        profile.getXingUrl()
                ),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.FACEBOOK_URL,
                        profile.getFacebookUrl()
                ),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.TWITTER_URL,
                        profile.getTwitterUrl()
                ),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.INSTAGRAM_URL,
                        profile.getInstagramUrl()
                ),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.ADDITIONAL_INFORMATION,
                        profile
                                .getAdditionalInformation()
                ),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.ACADEMIC_DEGREE,
                        profile.getAcademicDegree()
                ),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.FIELD_OF_STUDY,
                        profile.getFieldOfStudy()
                ),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.JOB_TITLE,
                        profile.getJobTitle()
                ),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.COMPANY,
                        profile.getCompany()
                ),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.POSITION,
                        profile.getPosition()
                ),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.WEBSITE,
                        profile.getWebsite()
                ),
                profileVisible(
                        privacyContext,
                        profile,
                        PrivacyField.EMPLOYMENT_STATUS,
                        profile.getEmploymentStatus()
                )
        );
    }

    private AddressResponse toAddressResponse(
            Address address,
            PrivacyAccessContext privacyContext
    ) {

        return new AddressResponse(
                address.getId(),
                addressVisible(
                        privacyContext,
                        address,
                        PrivacyField.ADDRESS_TYPE,
                        address.getType()
                ),
                addressVisible(
                        privacyContext,
                        address,
                        PrivacyField.ADDRESS_STREET,
                        address.getStreet()
                ),
                addressVisible(
                        privacyContext,
                        address,
                        PrivacyField.ADDRESS_HOUSE_NUMBER,
                        address.getHouseNumber()
                ),
                addressVisible(
                        privacyContext,
                        address,
                        PrivacyField.ADDRESS_POSTAL_CODE,
                        address.getPostalCode()
                ),
                addressVisible(
                        privacyContext,
                        address,
                        PrivacyField.ADDRESS_CITY,
                        address.getCity()
                ),
                addressVisible(
                        privacyContext,
                        address,
                        PrivacyField.ADDRESS_COUNTRY,
                        address.getCountry()
                )
        );
    }

    private CorpsMembershipResponse toMembershipResponse(
            CorpsMembership membership,
            PrivacyAccessContext privacyContext
    ) {

        boolean corpsVisible =
                canViewMembershipField(
                        privacyContext,
                        membership,
                        PrivacyField.CORPS_NAME
                );

        UUID leibburschId =
                null;

        if (
                canViewMembershipField(
                        privacyContext,
                        membership,
                        PrivacyField.LEIBBURSCH
                )
                && membership.getLeibbursch()
                        != null
        ) {
            leibburschId =
                    membership
                            .getLeibbursch()
                            .getId();
        }

        return new CorpsMembershipResponse(
                membership.getId(),
                corpsVisible
                        ? membership
                                .getCorps()
                                .getId()
                        : null,
                corpsVisible
                        ? membership
                                .getCorps()
                                .getName()
                        : null,
                membershipVisible(
                        privacyContext,
                        membership,
                        PrivacyField.NAME_IN_CORPS,
                        membership.getNameInCorps()
                ),
                membershipVisible(
                        privacyContext,
                        membership,
                        PrivacyField.CORPS_LIST_NUMBER,
                        membership.getCorpsListNumber()
                ),
                membershipVisible(
                        privacyContext,
                        membership,
                        PrivacyField.BAND_NUMBER,
                        membership.getBandNumber()
                ),
                membershipVisible(
                        privacyContext,
                        membership,
                        PrivacyField.BRACKETS,
                        membership.getBrackets()
                ),
                membershipVisible(
                        privacyContext,
                        membership,
                        PrivacyField.MEMBERSHIP_STATUS,
                        membership.getMembershipStatus()
                ),
                membershipVisible(
                        privacyContext,
                        membership,
                        PrivacyField.ADMISSION_DATE,
                        membership.getAdmissionDate()
                ),
                membershipVisible(
                        privacyContext,
                        membership,
                        PrivacyField.RECEPTION_DATE,
                        membership.getReceptionDate()
                ),
                membershipVisible(
                        privacyContext,
                        membership,
                        PrivacyField.PHILISTRATION_DATE,
                        membership.getPhilistrationDate()
                ),
                membershipVisible(
                        privacyContext,
                        membership,
                        PrivacyField.RECEPTION_PHOTO,
                        membership.getReceptionPhoto()
                ),
                leibburschId
        );
    }

    private boolean canViewProfileField(
            PrivacyAccessContext privacyContext,
            PersonProfile profile,
            PrivacyField field
    ) {

        return privacyContext.canView(
                PrivacyResourceType.PROFILE,
                profile.getId(),
                field
        );
    }

    private boolean canViewMembershipField(
            PrivacyAccessContext privacyContext,
            CorpsMembership membership,
            PrivacyField field
    ) {

        return privacyContext.canView(
                PrivacyResourceType.CORPS_MEMBERSHIP,
                membership.getId(),
                field
        );
    }

    private <T> T profileVisible(
            PrivacyAccessContext privacyContext,
            PersonProfile profile,
            PrivacyField field,
            T value
    ) {

        return canViewProfileField(
                privacyContext,
                profile,
                field
        )
                ? value
                : null;
    }

    private <T> T addressVisible(
            PrivacyAccessContext privacyContext,
            Address address,
            PrivacyField field,
            T value
    ) {

        return privacyContext.canView(
                PrivacyResourceType.ADDRESS,
                address.getId(),
                field
        )
                ? value
                : null;
    }

    private <T> T membershipVisible(
            PrivacyAccessContext privacyContext,
            CorpsMembership membership,
            PrivacyField field,
            T value
    ) {

        return canViewMembershipField(
                privacyContext,
                membership,
                field
        )
                ? value
                : null;
    }
}