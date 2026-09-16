package de.franconia.tuebingen.adh.auth;

public class EmailAlreadyExistsException
        extends RuntimeException {

    public EmailAlreadyExistsException() {
        super(
                "Für diese E-Mail-Adresse existiert bereits ein Benutzer"
        );
    }
}