package de.franconia.tuebingen.adh.auth;

import de.franconia.tuebingen.adh.user.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    RefreshTokenRepository repository;

    private final JwtProperties properties = new JwtProperties(
            "issuer", "audience", Duration.ofMinutes(15), Duration.ofDays(30));

    @Test
    void createStoresHashedExpiringToken() {
        User user = new User();
        String token = new RefreshTokenService(repository, properties).create(user);

        assertNotNull(token);
        verify(repository).save(any(RefreshToken.class));
    }

    @Test
    void consumeRejectsExpiredToken() {
        RefreshToken token = new RefreshToken();
        token.setExpiresAt(Instant.now().minusSeconds(1));
        when(repository.findByTokenHash(any(String.class))).thenReturn(Optional.of(token));

        assertThrows(InvalidRefreshTokenException.class,
                () -> new RefreshTokenService(repository, properties).consumeAndRotate("expired"));
    }

    @Test
    void revokeIgnoresUnknownToken() {
        when(repository.findByTokenHash(any(String.class))).thenReturn(Optional.empty());

        new RefreshTokenService(repository, properties).revoke("unknown");

        verify(repository).findByTokenHash(any(String.class));
    }
}