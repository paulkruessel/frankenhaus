package de.franconia.tuebingen.adh.profile;

public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException() {
        super("Profil wurde nicht gefunden");
    }
}