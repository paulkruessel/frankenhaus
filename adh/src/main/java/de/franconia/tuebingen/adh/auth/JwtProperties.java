package de.franconia.tuebingen.adh.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(

        String issuer,

        String audience,

        Duration accessTokenLifetime,

        Duration refreshTokenLifetime

) {
}