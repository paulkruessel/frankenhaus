package de.franconia.tuebingen.adh.corps;

public class CorpsNotFoundException
        extends RuntimeException {

    public CorpsNotFoundException() {
        super("Corps wurde nicht gefunden");
    }
}