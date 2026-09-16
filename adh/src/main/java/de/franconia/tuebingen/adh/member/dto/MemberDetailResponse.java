package de.franconia.tuebingen.adh.member.dto;

import de.franconia.tuebingen.adh.corps.dto.CorpsMembershipResponse;
import de.franconia.tuebingen.adh.profile.dto.AddressResponse;
import de.franconia.tuebingen.adh.profile.dto.ProfileResponse;

import java.util.List;

public record MemberDetailResponse(

        ProfileResponse profile,

        List<AddressResponse> addresses,

        List<CorpsMembershipResponse> corpsMemberships

) {
}