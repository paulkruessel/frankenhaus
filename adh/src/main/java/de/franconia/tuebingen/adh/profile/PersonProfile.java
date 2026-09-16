package de.franconia.tuebingen.adh.profile;

import de.franconia.tuebingen.adh.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "person_profiles")
public class PersonProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private User user;

    @Column(length = 100)
    private String title;

    @Column(name = "first_name", length = 255)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 255)
    private String lastName;

    @Column(name = "mobile_phone", length = 100)
    private String mobilePhone;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "birth_place", length = 255)
    private String birthPlace;

    @Column(name = "wikipedia_url", columnDefinition = "TEXT")
    private String wikipediaUrl;

    @Column(name = "linkedin_url", columnDefinition = "TEXT")
    private String linkedinUrl;

    @Column(name = "xing_url", columnDefinition = "TEXT")
    private String xingUrl;

    @Column(name = "facebook_url", columnDefinition = "TEXT")
    private String facebookUrl;

    @Column(name = "twitter_url", columnDefinition = "TEXT")
    private String twitterUrl;

    @Column(name = "instagram_url", columnDefinition = "TEXT")
    private String instagramUrl;

    @Column(name = "additional_information", columnDefinition = "TEXT")
    private String additionalInformation;

    @Column(name = "academic_degree", length = 255)
    private String academicDegree;

    @Column(name = "field_of_study", length = 255)
    private String fieldOfStudy;

    @Column(name = "job_title", length = 255)
    private String jobTitle;

    @Column(length = 255)
    private String company;

    @Column(length = 255)
    private String position;

    @Column(columnDefinition = "TEXT")
    private String website;

    @Column(name = "employment_status", length = 100)
    private String employmentStatus;
}