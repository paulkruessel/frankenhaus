package de.franconia.tuebingen.adh.auth;

import de.franconia.tuebingen.adh.auth.dto.LoginRequest;
import de.franconia.tuebingen.adh.auth.dto.RegisterRequest;
import de.franconia.tuebingen.adh.profile.PersonProfile;
import de.franconia.tuebingen.adh.profile.ProfileRepository;
import de.franconia.tuebingen.adh.user.User;
import de.franconia.tuebingen.adh.user.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    ProfileRepository profileRepository;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtService jwtService;
    @Mock
    RefreshTokenService refreshTokenService;
    @Mock
    Authentication authentication;

    private AuthService service;

    @BeforeEach
    void setUp() {
        service = new AuthService(
                userRepository,
                profileRepository,
                authenticationManager,
                passwordEncoder,
                jwtService,
                refreshTokenService);
    }

    @Test
    void registerNormalizesCredentialsAndCreatesProfileAndTokens() {
        when(passwordEncoder.encode("a secure password")).thenReturn("encoded");
        when(jwtService.createAccessToken(any())).thenReturn("access");
        when(jwtService.accessTokenLifetimeSeconds()).thenReturn(900L);
        when(refreshTokenService.create(any())).thenReturn("refresh");
        when(userRepository.existsByEmail("person@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(UUID.randomUUID());
            return user;
        });

        AuthService.AuthenticationResult result = service.register(
                new RegisterRequest(" Person@Example.com ", "a secure password", " Ada ", " Lovelace "));

        assertEquals("access", result.response().accessToken());
        assertEquals("Bearer", result.response().tokenType());
        assertEquals("refresh", result.refreshToken());
        verify(userRepository).save(any(User.class));
        verify(profileRepository).save(any(PersonProfile.class));
    }

    @Test
    void registerRejectsExistingEmail() {
        when(userRepository.existsByEmail("person@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> service.register(
                new RegisterRequest("PERSON@example.com", "a secure password", "Ada", "Lovelace")));
    }

    @Test
    void loginUsesNormalizedEmailAndIssuesTokens() {
        when(jwtService.createAccessToken(any())).thenReturn("access");
        when(jwtService.accessTokenLifetimeSeconds()).thenReturn(900L);
        when(refreshTokenService.create(any())).thenReturn("refresh");
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new AppUserPrincipal(user));
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        AuthService.AuthenticationResult result = service.login(
                new LoginRequest(" PERSON@example.com ", "a secure password"));

        assertEquals("access", result.response().accessToken());
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void refreshRejectsDisabledUserAndLogoutRevokesToken() {
        User user = new User();
        user.setEnabled(false);
        when(refreshTokenService.consumeAndRotate("refresh")).thenReturn(user);

        assertThrows(InvalidRefreshTokenException.class, () -> service.refresh("refresh"));

        service.logout("refresh");
        verify(refreshTokenService).revoke(eq("refresh"));
    }
}