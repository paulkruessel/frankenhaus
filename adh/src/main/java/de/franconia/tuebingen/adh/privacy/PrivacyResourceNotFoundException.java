package de.franconia.tuebingen.adh.privacy;

public class PrivacyResourceNotFoundException
        extends RuntimeException {

    public PrivacyResourceNotFoundException() {
        super("Ressource wurde nicht gefunden");
    }
}