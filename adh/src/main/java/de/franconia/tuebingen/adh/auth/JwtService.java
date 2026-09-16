package de.franconia.tuebingen.adh.auth;

import de.franconia.tuebingen.adh.user.User;

import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;

import org.springframework.security.oauth2.jwt.*;

import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties properties;

    public JwtService(
            JwtEncoder jwtEncoder,
            JwtProperties properties
    ) {
        this.jwtEncoder = jwtEncoder;
        this.properties = properties;
    }

    public String createAccessToken(User user) {

        Instant now = Instant.now();

        Instant expiresAt =
                now.plus(
                        properties.accessTokenLifetime()
                );

        JwtClaimsSet claims =
                JwtClaimsSet.builder()

                        .issuer(
                                properties.issuer()
                        )

                        .subject(
                                user.getId().toString()
                        )

                        .audience(
                                java.util.List.of(
                                        properties.audience()
                                )
                        )

                        .issuedAt(now)
                        .expiresAt(expiresAt)

                        .claim(
                                "email",
                                user.getEmail()
                        )

                        .claim(
                                "roles",
                                user.getRoles()
                                        .stream()
                                        .map(Enum::name)
                                        .toList()
                        )

                        .build();

        JwsHeader header =
                JwsHeader
                        .with(SignatureAlgorithm.RS256)
                        .type("JWT")
                        .build();

        return jwtEncoder
                .encode(
                        JwtEncoderParameters.from(
                                header,
                                claims
                        )
                )
                .getTokenValue();
    }

    public long accessTokenLifetimeSeconds() {
        return properties
                .accessTokenLifetime()
                .toSeconds();
    }
}