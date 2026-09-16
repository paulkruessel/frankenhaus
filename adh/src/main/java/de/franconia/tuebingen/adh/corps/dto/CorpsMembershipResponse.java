package de.franconia.tuebingen.adh.corps.dto;

import de.franconia.tuebingen.adh.corps.MembershipStatus;

import java.time.LocalDate;
import java.util.UUID;

public record CorpsMembershipResponse(

        UUID id,

        UUID corpsId,

        String corpsName,

        String nameInCorps,

        String corpsListNumber,

        String bandNumber,

        String brackets,

        MembershipStatus membershipStatus,

        LocalDate admissionDate,

        LocalDate receptionDate,

        LocalDate philistrationDate,

        String receptionPhoto,

        UUID leibburschMembershipId

) {
}