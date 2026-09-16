package de.franconia.tuebingen.adh.auth;

import jakarta.servlet.http.Cookie;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenCookieService {

    private static final String COOKIE_NAME =
            "refresh_token";

    private final JwtProperties properties;

    public RefreshTokenCookieService(
            JwtProperties properties
    ) {
        this.properties = properties;
    }

    public ResponseCookie create(
            String refreshToken
    ) {

        return ResponseCookie
                .from(
                        COOKIE_NAME,
                        refreshToken
                )
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/auth")
                .maxAge(
                        properties
                                .refreshTokenLifetime()
                )
                .build();
    }

    public ResponseCookie delete() {

        return ResponseCookie
                .from(
                        COOKIE_NAME,
                        ""
                )
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/auth")
                .maxAge(0)
                .build();
    }

    public String cookieName() {
        return COOKIE_NAME;
    }
}