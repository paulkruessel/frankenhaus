package de.franconia.tuebingen.adh.auth;

public class InvalidRefreshTokenException
        extends RuntimeException {

    public InvalidRefreshTokenException() {
        super(
                "Refresh Token ist ungültig oder abgelaufen"
        );
    }
}