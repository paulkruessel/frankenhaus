package de.franconia.tuebingen.adh.profile.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ProfileResponse(

        UUID id,

        UUID userId,

        String email,

        String title,

        String firstName,

        String lastName,

        String mobilePhone,

        LocalDate birthDate,

        String birthPlace,

        String wikipediaUrl,

        String linkedinUrl,

        String xingUrl,

        String facebookUrl,

        String twitterUrl,

        String instagramUrl,

        String additionalInformation,

        String academicDegree,

        String fieldOfStudy,

        String jobTitle,

        String company,

        String position,

        String website,

        String employmentStatus

) {
}