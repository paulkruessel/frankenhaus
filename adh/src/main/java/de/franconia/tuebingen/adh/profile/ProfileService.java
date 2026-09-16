package de.franconia.tuebingen.adh.profile;

import de.franconia.tuebingen.adh.auth.EmailAlreadyExistsException;
import de.franconia.tuebingen.adh.profile.dto.ProfileResponse;
import de.franconia.tuebingen.adh.profile.dto.UpdateProfileRequest;
import de.franconia.tuebingen.adh.user.User;
import de.franconia.tuebingen.adh.user.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileService(
            ProfileRepository profileRepository,
            UserRepository userRepository
    ) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public ProfileResponse getOwnProfile(UUID userId) {

        PersonProfile profile = profileRepository
                .findByUserId(userId)
                .orElseThrow(ProfileNotFoundException::new);

        return toResponse(profile);
    }

    @Transactional
    public ProfileResponse updateOwnProfile(
            UUID userId,
            UpdateProfileRequest request
    ) {

        PersonProfile profile = profileRepository
                .findByUserId(userId)
                .orElseThrow(ProfileNotFoundException::new);

        User user = profile.getUser();

        updateEmail(user, request.email());

        profile.setTitle(
                normalizeNullable(request.title())
        );

        profile.setFirstName(
                normalizeNullable(request.firstName())
        );

        profile.setLastName(
                request.lastName().trim()
        );

        profile.setMobilePhone(
                normalizeNullable(request.mobilePhone())
        );

        profile.setBirthDate(
                request.birthDate()
        );

        profile.setBirthPlace(
                normalizeNullable(request.birthPlace())
        );

        profile.setWikipediaUrl(
                normalizeNullable(request.wikipediaUrl())
        );

        profile.setLinkedinUrl(
                normalizeNullable(request.linkedinUrl())
        );

        profile.setXingUrl(
                normalizeNullable(request.xingUrl())
        );

        profile.setFacebookUrl(
                normalizeNullable(request.facebookUrl())
        );

        profile.setTwitterUrl(
                normalizeNullable(request.twitterUrl())
        );

        profile.setInstagramUrl(
                normalizeNullable(request.instagramUrl())
        );

        profile.setAdditionalInformation(
                normalizeNullable(
                        request.additionalInformation()
                )
        );

        profile.setAcademicDegree(
                normalizeNullable(request.academicDegree())
        );

        profile.setFieldOfStudy(
                normalizeNullable(request.fieldOfStudy())
        );

        profile.setJobTitle(
                normalizeNullable(request.jobTitle())
        );

        profile.setCompany(
                normalizeNullable(request.company())
        );

        profile.setPosition(
                normalizeNullable(request.position())
        );

        profile.setWebsite(
                normalizeNullable(request.website())
        );

        profile.setEmploymentStatus(
                normalizeNullable(request.employmentStatus())
        );

        /*
         * Kein profileRepository.save(profile) notwendig.
         *
         * profile und user sind innerhalb dieser Transaktion
         * managed JPA entities.
         *
         * Hibernate erkennt die Änderungen automatisch
         * per Dirty Checking.
         */

        return toResponse(profile);
    }

    private void updateEmail(
            User user,
            String requestedEmail
    ) {

        String email = requestedEmail
                .trim()
                .toLowerCase(Locale.ROOT);

        if (email.equals(user.getEmail())) {
            return;
        }

        userRepository
                .findByEmail(email)
                .filter(existingUser ->
                        !existingUser
                                .getId()
                                .equals(user.getId())
                )
                .ifPresent(existingUser -> {
                    throw new EmailAlreadyExistsException();
                });

        user.setEmail(email);
    }

    private ProfileResponse toResponse(
            PersonProfile profile
    ) {

        User user = profile.getUser();

        return new ProfileResponse(
                profile.getId(),
                user.getId(),
                user.getEmail(),
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

    private String normalizeNullable(
            String value
    ) {

        if (value == null) {
            return null;
        }

        String trimmed = value.trim();

        return trimmed.isEmpty()
                ? null
                : trimmed;
    }
}