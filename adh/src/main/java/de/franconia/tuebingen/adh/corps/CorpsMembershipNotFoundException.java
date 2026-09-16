package de.franconia.tuebingen.adh.corps;

public class CorpsMembershipNotFoundException
        extends RuntimeException {

    public CorpsMembershipNotFoundException() {
        super("Corps-Mitgliedschaft wurde nicht gefunden");
    }
}