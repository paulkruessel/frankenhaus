package de.franconia.tuebingen.adh.corps.dto;

import de.franconia.tuebingen.adh.corps.MembershipStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record CreateCorpsMembershipRequest(

        @NotNull(
                message = "Corps muss angegeben werden"
        )
        UUID corpsId,

        @Size(max = 255)
        String nameInCorps,

        @Size(max = 100)
        String corpsListNumber,

        @Size(max = 100)
        String bandNumber,

        @Size(max = 255)
        String brackets,

        MembershipStatus membershipStatus,

        LocalDate admissionDate,

        LocalDate receptionDate,

        LocalDate philistrationDate,

        @Size(max = 2048)
        String receptionPhoto,

        UUID leibburschMembershipId

) {
}