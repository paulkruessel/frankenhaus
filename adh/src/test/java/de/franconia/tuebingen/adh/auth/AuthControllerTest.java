package de.franconia.tuebingen.adh.auth;

import de.franconia.tuebingen.adh.auth.dto.AuthResponse;
import de.franconia.tuebingen.adh.auth.dto.LoginRequest;
import de.franconia.tuebingen.adh.auth.dto.RegisterRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    AuthService authService;
    @Mock
    RefreshTokenCookieService cookieService;

    private final AuthResponse response = new AuthResponse("access", "Bearer", 900);

    @Test
    void registerReturnsCreatedResponseAndSetsCookie() {
        when(authService.register(any())).thenReturn(result("refresh"));
        when(cookieService.create("refresh")).thenReturn(cookie("refresh"));

        var result = new AuthController(authService, cookieService).register(
                new RegisterRequest("person@example.com", "a secure password", "Ada", "Lovelace"));

        assertEquals(HttpStatus.CREATED.value(), result.getStatusCode().value());
        assertEquals(response, result.getBody());
        verify(cookieService).create("refresh");
    }

    @Test
    void loginReturnsOkResponseAndSetsCookie() {
        when(authService.login(any())).thenReturn(result("refresh"));
        when(cookieService.create("refresh")).thenReturn(cookie("refresh"));

        var result = new AuthController(authService, cookieService).login(
                new LoginRequest("person@example.com", "a secure password"));

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());
        assertEquals(response, result.getBody());
    }

    @Test
    void refreshRotatesTokenAndReturnsOk() {
        when(authService.refresh("old-refresh")).thenReturn(result("new-refresh"));
        when(cookieService.create("new-refresh")).thenReturn(cookie("new-refresh"));

        var result = new AuthController(authService, cookieService).refresh("old-refresh");

        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());
        verify(authService).refresh("old-refresh");
    }

    @Test
    void logoutRevokesProvidedTokenAndDeletesCookie() {
        when(cookieService.delete()).thenReturn(cookie(""));

        var result = new AuthController(authService, cookieService).logout("refresh");

        assertEquals(HttpStatus.NO_CONTENT.value(), result.getStatusCode().value());
        verify(authService).logout("refresh");
        verify(cookieService).delete();
    }

    private AuthService.AuthenticationResult result(String refreshToken) {
        return new AuthService.AuthenticationResult(response, refreshToken);
    }

    private ResponseCookie cookie(String value) {
        return ResponseCookie.from("refresh_token", value)
                .maxAge(Duration.ofDays(30))
                .build();
    }
}