package de.franconia.tuebingen.adh.profile;

public class AddressNotFoundException
        extends RuntimeException {

    public AddressNotFoundException() {
        super("Adresse wurde nicht gefunden");
    }
}