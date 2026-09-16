package de.franconia.tuebingen.adh.corps;

public class CorpsMembershipAlreadyExistsException
        extends RuntimeException {

    public CorpsMembershipAlreadyExistsException() {
        super("Für dieses Corps existiert bereits eine Mitgliedschaft");
    }
}