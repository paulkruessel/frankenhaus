package de.franconia.tuebingen.adh.profile;

import de.franconia.tuebingen.adh.profile.dto.ProfileResponse;
import de.franconia.tuebingen.adh.profile.dto.UpdateProfileRequest;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(
            ProfileService profileService
    ) {
        this.profileService = profileService;
    }

    @GetMapping("/me")
    public ProfileResponse getOwnProfile(
            @AuthenticationPrincipal Jwt jwt
    ) {

        UUID userId =
                UUID.fromString(
                        jwt.getSubject()
                );

        return profileService
                .getOwnProfile(userId);
    }

    @PutMapping("/me")
    public ProfileResponse updateOwnProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid
            @RequestBody
            UpdateProfileRequest request
    ) {

        UUID userId =
                UUID.fromString(
                        jwt.getSubject()
                );

        return profileService
                .updateOwnProfile(
                        userId,
                        request
                );
    }
}