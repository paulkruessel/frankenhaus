package de.franconia.tuebingen.adh.member.dto;

import java.util.UUID;

public record MemberSummaryResponse(

        UUID id,

        String title,

        String firstName,

        String lastName,

        String email,

        String academicDegree,

        String jobTitle,

        String company,

        String position

) {
}