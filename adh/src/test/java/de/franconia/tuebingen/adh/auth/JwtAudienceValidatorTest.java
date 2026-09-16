package de.franconia.tuebingen.adh.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtAudienceValidatorTest {

    @Test
    void acceptsConfiguredAudienceAndRejectsOtherAudience() {
        JwtAudienceValidator validator = new JwtAudienceValidator("adh-api");
        Jwt valid = jwt(List.of("adh-api"));
        Jwt invalid = jwt(List.of("other-api"));

        assertTrue(validator.validate(valid).hasErrors() == false);
        assertFalse(validator.validate(invalid).hasErrors() == false);
    }

    private Jwt jwt(List<String> audience) {
        return new Jwt("token", Instant.now(), Instant.now().plusSeconds(900),
                Map.of("alg", "none"), Map.of("aud", audience));
    }
}