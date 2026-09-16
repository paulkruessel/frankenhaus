package de.franconia.tuebingen.adh.auth;

import de.franconia.tuebingen.adh.user.User;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import java.time.Instant;

import java.util.Base64;
import java.util.HexFormat;

@Service
public class RefreshTokenService {

    private static final int TOKEN_BYTES = 64;

    private final SecureRandom secureRandom =
            new SecureRandom();

    private final RefreshTokenRepository repository;
    private final JwtProperties properties;

    public RefreshTokenService(
            RefreshTokenRepository repository,
            JwtProperties properties
    ) {
        this.repository = repository;
        this.properties = properties;
    }

    @Transactional
    public String create(User user) {

        byte[] randomBytes =
                new byte[TOKEN_BYTES];

        secureRandom.nextBytes(randomBytes);

        String rawToken =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(
                                randomBytes
                        );

        RefreshToken refreshToken =
                new RefreshToken();

        refreshToken.setUser(user);

        refreshToken.setTokenHash(
                hash(rawToken)
        );

        refreshToken.setCreatedAt(
                Instant.now()
        );

        refreshToken.setExpiresAt(
                Instant.now().plus(
                        properties
                                .refreshTokenLifetime()
                )
        );

        repository.save(refreshToken);

        return rawToken;
    }

    @Transactional
    public User consumeAndRotate(
            String rawToken
    ) {

        RefreshToken storedToken =
                repository
                        .findByTokenHash(
                                hash(rawToken)
                        )
                        .orElseThrow(
                                InvalidRefreshTokenException::new
                        );

        if (!storedToken.isValid()) {
            throw new InvalidRefreshTokenException();
        }

        storedToken.setRevokedAt(
                Instant.now()
        );

        return storedToken.getUser();
    }

    @Transactional
    public void revoke(
            String rawToken
    ) {

        repository
                .findByTokenHash(
                        hash(rawToken)
                )
                .ifPresent(token -> {

                    if (!token.isRevoked()) {
                        token.setRevokedAt(
                                Instant.now()
                        );
                    }
                });
    }

    private String hash(String token) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );

            byte[] hash =
                    digest.digest(
                            token.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            return HexFormat
                    .of()
                    .formatHex(hash);

        } catch (
                NoSuchAlgorithmException exception
        ) {

            throw new IllegalStateException(
                    "SHA-256 ist nicht verfügbar",
                    exception
            );
        }
    }
}