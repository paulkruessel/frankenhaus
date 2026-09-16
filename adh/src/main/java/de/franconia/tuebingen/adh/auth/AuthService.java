package de.franconia.tuebingen.adh.auth;

import de.franconia.tuebingen.adh.auth.dto.AuthResponse;
import de.franconia.tuebingen.adh.auth.dto.LoginRequest;
import de.franconia.tuebingen.adh.auth.dto.RegisterRequest;

import de.franconia.tuebingen.adh.profile.PersonProfile;
import de.franconia.tuebingen.adh.profile.ProfileRepository;

import de.franconia.tuebingen.adh.user.Role;
import de.franconia.tuebingen.adh.user.User;
import de.franconia.tuebingen.adh.user.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UserRepository userRepository,
            ProfileRepository profileRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService
    ) {

        this.userRepository =
                userRepository;

        this.profileRepository =
                profileRepository;

        this.authenticationManager =
                authenticationManager;

        this.passwordEncoder =
                passwordEncoder;

        this.jwtService =
                jwtService;

        this.refreshTokenService =
                refreshTokenService;
    }

    @Transactional
    public AuthenticationResult register(
            RegisterRequest request
    ) {

        String email =
                normalizeEmail(
                        request.email()
                );

        if (
                userRepository
                        .existsByEmail(email)
        ) {

            throw new EmailAlreadyExistsException();
        }

        User user =
                new User();

        user.setEmail(email);

        user.setPasswordHash(
                passwordEncoder.encode(
                        request.password()
                )
        );

        user.setEnabled(true);

        user.getRoles().add(
                Role.USER
        );

        userRepository.save(user);

        PersonProfile profile =
                new PersonProfile();

        profile.setUser(user);

        profile.setFirstName(
                request
                        .firstName()
                        .trim()
        );

        profile.setLastName(
                request
                        .lastName()
                        .trim()
        );

        profileRepository.save(profile);

        return issueTokens(user);
    }

    @Transactional
    public AuthenticationResult login(
            LoginRequest request
    ) {

        String email =
                normalizeEmail(
                        request.email()
                );

        Authentication authentication =
                authenticationManager
                        .authenticate(
                                new UsernamePasswordAuthenticationToken(
                                        email,
                                        request.password()
                                )
                        );

        AppUserPrincipal principal =
                (AppUserPrincipal)
                        authentication
                                .getPrincipal();

        User user =
                userRepository
                        .findById(
                                principal.getId()
                        )
                        .orElseThrow();

        return issueTokens(user);
    }

    @Transactional
    public AuthenticationResult refresh(
            String refreshToken
    ) {

        User user =
                refreshTokenService
                        .consumeAndRotate(
                                refreshToken
                        );

        if (!user.isEnabled()) {
            throw new InvalidRefreshTokenException();
        }

        return issueTokens(user);
    }

    @Transactional
    public void logout(
            String refreshToken
    ) {

        refreshTokenService.revoke(
                refreshToken
        );
    }

    private AuthenticationResult issueTokens(
            User user
    ) {

        String accessToken =
                jwtService
                        .createAccessToken(
                                user
                        );

        String refreshToken =
                refreshTokenService
                        .create(user);

        AuthResponse response =
                new AuthResponse(
                        accessToken,
                        "Bearer",
                        jwtService
                                .accessTokenLifetimeSeconds()
                );

        return new AuthenticationResult(
                response,
                refreshToken
        );
    }

    private String normalizeEmail(
            String email
    ) {

        return email
                .trim()
                .toLowerCase(
                        Locale.ROOT
                );
    }

    public record AuthenticationResult(

            AuthResponse response,

            String refreshToken

    ) {
    }
}