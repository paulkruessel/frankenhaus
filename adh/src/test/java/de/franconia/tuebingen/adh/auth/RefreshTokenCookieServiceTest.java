package de.franconia.tuebingen.adh.auth;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RefreshTokenCookieServiceTest {

    private final RefreshTokenCookieService service = new RefreshTokenCookieService(
            new JwtProperties("issuer", "audience", Duration.ofMinutes(15), Duration.ofDays(30)));

    @Test
    void createsSecureHttpOnlyStrictCookieForAuthPath() {
        var cookie = service.create("refresh-value");

        assertEquals("refresh_token", cookie.getName());
        assertEquals("refresh-value", cookie.getValue());
        assertEquals("/api/auth", cookie.getPath());
        assertEquals(Duration.ofDays(30), cookie.getMaxAge());
        assertTrue(cookie.isHttpOnly());
        assertTrue(cookie.isSecure());
        assertEquals("Strict", cookie.getSameSite());
    }

    @Test
    void deleteExpiresCookieImmediately() {
        var cookie = service.delete();

        assertEquals("refresh_token", service.cookieName());
        assertEquals(Duration.ZERO, cookie.getMaxAge());
        assertEquals("", cookie.getValue());
    }
}