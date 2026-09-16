package de.franconia.tuebingen.adh.auth;

import de.franconia.tuebingen.adh.user.Role;
import de.franconia.tuebingen.adh.user.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    JwtEncoder encoder;

    @Test
    void createsBearerTokenWithConfiguredLifetime() {
        JwtProperties properties = new JwtProperties(
                "issuer", "audience", Duration.ofMinutes(15), Duration.ofDays(30));
        when(encoder.encode(any())).thenReturn(Jwt.withTokenValue("signed-token")
                .header("alg", "RS256")
                .claim("roles", java.util.List.of("USER"))
                .build());
        User user = new User();
        user.setId(java.util.UUID.randomUUID());
        user.setEmail("person@example.com");
        user.getRoles().add(Role.USER);

        JwtService service = new JwtService(encoder, properties);

        assertEquals("signed-token", service.createAccessToken(user));
        assertEquals(900, service.accessTokenLifetimeSeconds());
    }
}