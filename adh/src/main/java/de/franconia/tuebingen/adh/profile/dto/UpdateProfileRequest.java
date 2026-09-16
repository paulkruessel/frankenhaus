package de.franconia.tuebingen.adh.profile.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateProfileRequest(

        @NotBlank(message = "E-Mail darf nicht leer sein")
        @Email(message = "E-Mail muss gültig sein")
        @Size(
                max = 320,
                message = "E-Mail darf maximal 320 Zeichen lang sein"
        )
        String email,

        @Size(
                max = 100,
                message = "Titel darf maximal 100 Zeichen lang sein"
        )
        String title,

        @Size(
                max = 255,
                message = "Vorname darf maximal 255 Zeichen lang sein"
        )
        String firstName,

        @NotBlank(message = "Nachname darf nicht leer sein")
        @Size(
                max = 255,
                message = "Nachname darf maximal 255 Zeichen lang sein"
        )
        String lastName,

        @Size(
                max = 100,
                message = "Mobiltelefon darf maximal 100 Zeichen lang sein"
        )
        String mobilePhone,

        @PastOrPresent(
                message = "Geburtsdatum darf nicht in der Zukunft liegen"
        )
        LocalDate birthDate,

        @Size(
                max = 255,
                message = "Geburtsort darf maximal 255 Zeichen lang sein"
        )
        String birthPlace,

        @Size(max = 2048)
        @Pattern(
                regexp = "^$|^https?://.+",
                message = "Wikipedia-Link muss eine gültige HTTP(S)-URL sein"
        )
        String wikipediaUrl,

        @Size(max = 2048)
        @Pattern(
                regexp = "^$|^https?://.+",
                message = "LinkedIn-Link muss eine gültige HTTP(S)-URL sein"
        )
        String linkedinUrl,

        @Size(max = 2048)
        @Pattern(
                regexp = "^$|^https?://.+",
                message = "XING-Link muss eine gültige HTTP(S)-URL sein"
        )
        String xingUrl,

        @Size(max = 2048)
        @Pattern(
                regexp = "^$|^https?://.+",
                message = "Facebook-Link muss eine gültige HTTP(S)-URL sein"
        )
        String facebookUrl,

        @Size(max = 2048)
        @Pattern(
                regexp = "^$|^https?://.+",
                message = "Twitter-Link muss eine gültige HTTP(S)-URL sein"
        )
        String twitterUrl,

        @Size(max = 2048)
        @Pattern(
                regexp = "^$|^https?://.+",
                message = "Instagram-Link muss eine gültige HTTP(S)-URL sein"
        )
        String instagramUrl,

        @Size(
                max = 10000,
                message = "Weitere Angaben dürfen maximal 10000 Zeichen lang sein"
        )
        String additionalInformation,

        @Size(max = 255)
        String academicDegree,

        @Size(max = 255)
        String fieldOfStudy,

        @Size(max = 255)
        String jobTitle,

        @Size(max = 255)
        String company,

        @Size(max = 255)
        String position,

        @Size(max = 2048)
        @Pattern(
                regexp = "^$|^https?://.+",
                message = "Website muss eine gültige HTTP(S)-URL sein"
        )
        String website,

        @Size(max = 100)
        String employmentStatus

) {
}