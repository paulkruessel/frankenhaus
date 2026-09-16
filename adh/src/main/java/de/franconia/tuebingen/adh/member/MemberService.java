package de.franconia.tuebingen.adh.member;

import de.franconia.tuebingen.adh.common.dto.PageResponse;

import de.franconia.tuebingen.adh.corps.CorpsMembership;
import de.franconia.tuebingen.adh.corps.CorpsMembershipRepository;
import de.franconia.tuebingen.adh.corps.dto.CorpsMembershipResponse;

import de.franconia.tuebingen.adh.member.dto.MemberDetailResponse;
import de.franconia.tuebingen.adh.member.dto.MemberSummaryResponse;

import de.franconia.tuebingen.adh.profile.Address;
import de.franconia.tuebingen.adh.profile.AddressRepository;
import de.franconia.tuebingen.adh.profile.PersonProfile;
import de.franconia.tuebingen.adh.profile.ProfileRepository;

import de.franconia.tuebingen.adh.profile.dto.AddressResponse;
import de.franconia.tuebingen.adh.profile.dto.ProfileResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class MemberService {

    private final ProfileRepository profileRepository;
    private final AddressRepository addressRepository;
    private final CorpsMembershipRepository membershipRepository;

    public MemberService(
            ProfileRepository profileRepository,
            AddressRepository addressRepository,
            CorpsMembershipRepository membershipRepository
    ) {
        this.profileRepository = profileRepository;
        this.addressRepository = addressRepository;
        this.membershipRepository = membershipRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<MemberSummaryResponse> searchMembers(
            String query,
            int page,
            int size
    ) {

        String normalizedQuery =
                query == null
                        ? ""
                        : query
                                .trim()
                                .toLowerCase(Locale.ROOT);

        PageRequest pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Order.asc("lastName"),
                                Sort.Order.asc("firstName"),
                                Sort.Order.asc("id")
                        )
                );

        Page<MemberSummaryResponse> result =
                profileRepository
                        .searchMembers(
                                normalizedQuery,
                                pageable
                        )
                        .map(this::toSummaryResponse);

        return PageResponse.from(result);
    }

    @Transactional(readOnly = true)
    public MemberDetailResponse getMember(
            UUID userId
    ) {

        PersonProfile profile =
                profileRepository
                        .findEnabledMemberByUserId(userId)
                        .orElseThrow(
                                MemberNotFoundException::new
                        );

        List<AddressResponse> addresses =
                addressRepository
                        .findByUserId(userId)
                        .stream()
                        .map(this::toAddressResponse)
                        .toList();

        List<CorpsMembershipResponse> memberships =
                membershipRepository
                        .findByUserId(userId)
                        .stream()
                        .map(this::toMembershipResponse)
                        .toList();

        return new MemberDetailResponse(
                toProfileResponse(profile),
                addresses,
                memberships
        );
    }

    private MemberSummaryResponse toSummaryResponse(
            PersonProfile profile
    ) {

        return new MemberSummaryResponse(
                profile.getUser().getId(),
                profile.getTitle(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getUser().getEmail(),
                profile.getAcademicDegree(),
                profile.getJobTitle(),
                profile.getCompany(),
                profile.getPosition()
        );
    }

    private ProfileResponse toProfileResponse(
            PersonProfile profile
    ) {

        return new ProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getUser().getEmail(),
                profile.getTitle(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getMobilePhone(),
                profile.getBirthDate(),
                profile.getBirthPlace(),
                profile.getWikipediaUrl(),
                profile.getLinkedinUrl(),
                profile.getXingUrl(),
                profile.getFacebookUrl(),
                profile.getTwitterUrl(),
                profile.getInstagramUrl(),
                profile.getAdditionalInformation(),
                profile.getAcademicDegree(),
                profile.getFieldOfStudy(),
                profile.getJobTitle(),
                profile.getCompany(),
                profile.getPosition(),
                profile.getWebsite(),
                profile.getEmploymentStatus()
        );
    }

    private AddressResponse toAddressResponse(
            Address address
    ) {

        return new AddressResponse(
                address.getId(),
                address.getType(),
                address.getStreet(),
                address.getHouseNumber(),
                address.getPostalCode(),
                address.getCity(),
                address.getCountry()
        );
    }

    private CorpsMembershipResponse toMembershipResponse(
            CorpsMembership membership
    ) {

        UUID leibburschId =
                membership.getLeibbursch() == null
                        ? null
                        : membership
                                .getLeibbursch()
                                .getId();

        return new CorpsMembershipResponse(
                membership.getId(),
                membership.getCorps().getId(),
                membership.getCorps().getName(),
                membership.getNameInCorps(),
                membership.getCorpsListNumber(),
                membership.getBandNumber(),
                membership.getBrackets(),
                membership.getMembershipStatus(),
                membership.getAdmissionDate(),
                membership.getReceptionDate(),
                membership.getPhilistrationDate(),
                membership.getReceptionPhoto(),
                leibburschId
        );
    }
}