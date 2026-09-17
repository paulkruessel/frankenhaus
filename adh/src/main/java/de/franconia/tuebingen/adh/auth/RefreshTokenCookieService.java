package de.franconia.tuebingen.adh.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenCookieService {

        private static final String COOKIE_NAME = "refresh_token";

        private final JwtProperties properties;
        private final boolean secure;

        public RefreshTokenCookieService(JwtProperties properties) {
                this(properties, true);
        }

        @Autowired
        public RefreshTokenCookieService(
                        JwtProperties properties,
                        @Value("${security.refresh-cookie-secure:true}") boolean secure) {
                this.properties = properties;
                this.secure = secure;
        }

        public ResponseCookie create(
                        String refreshToken) {

                return ResponseCookie
                                .from(
                                                COOKIE_NAME,
                                                refreshToken)
                                .httpOnly(true)
                                .secure(secure)
                                .sameSite("Strict")
                                .path("/api/auth")
                                .maxAge(
                                                properties
                                                                .refreshTokenLifetime())
                                .build();
        }

        public ResponseCookie delete() {

                return ResponseCookie
                                .from(
                                                COOKIE_NAME,
                                                "")
                                .httpOnly(true)
                                .secure(secure)
                                .sameSite("Strict")
                                .path("/api/auth")
                                .maxAge(0)
                                .build();
        }

        public String cookieName() {
                return COOKIE_NAME;
        }
}