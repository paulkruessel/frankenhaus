package de.franconia.tuebingen.adh.auth;

import de.franconia.tuebingen.adh.auth.dto.AuthResponse;
import de.franconia.tuebingen.adh.auth.dto.LoginRequest;
import de.franconia.tuebingen.adh.auth.dto.RegisterRequest;

import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    private final RefreshTokenCookieService
            cookieService;

    public AuthController(
            AuthService authService,
            RefreshTokenCookieService cookieService
    ) {

        this.authService =
                authService;

        this.cookieService =
                cookieService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid
            @RequestBody
            RegisterRequest request
    ) {

        AuthService.AuthenticationResult result =
                authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)

                .header(
                        HttpHeaders.SET_COOKIE,
                        cookieService
                                .create(
                                        result.refreshToken()
                                )
                                .toString()
                )

                .body(
                        result.response()
                );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid
            @RequestBody
            LoginRequest request
    ) {

        AuthService.AuthenticationResult result =
                authService.login(request);

        return ResponseEntity
                .ok()

                .header(
                        HttpHeaders.SET_COOKIE,
                        cookieService
                                .create(
                                        result.refreshToken()
                                )
                                .toString()
                )

                .body(
                        result.response()
                );
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(
                    name = "refresh_token"
            )
            String refreshToken
    ) {

        AuthService.AuthenticationResult result =
                authService.refresh(
                        refreshToken
                );

        return ResponseEntity
                .ok()

                .header(
                        HttpHeaders.SET_COOKIE,
                        cookieService
                                .create(
                                        result.refreshToken()
                                )
                                .toString()
                )

                .body(
                        result.response()
                );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(
                    name = "refresh_token",
                    required = false
            )
            String refreshToken
    ) {

        if (refreshToken != null) {
            authService.logout(
                    refreshToken
            );
        }

        return ResponseEntity
                .noContent()

                .header(
                        HttpHeaders.SET_COOKIE,
                        cookieService
                                .delete()
                                .toString()
                )

                .build();
    }
}