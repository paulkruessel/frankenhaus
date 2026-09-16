package de.franconia.tuebingen.adh.corps;

public class CorpsAlreadyExistsException
        extends RuntimeException {

    public CorpsAlreadyExistsException() {
        super("Ein Corps mit diesem Namen existiert bereits");
    }
}