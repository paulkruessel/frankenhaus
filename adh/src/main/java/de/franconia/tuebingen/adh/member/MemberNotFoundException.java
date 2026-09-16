package de.franconia.tuebingen.adh.member;

public class MemberNotFoundException
        extends RuntimeException {

    public MemberNotFoundException() {
        super("Mitglied wurde nicht gefunden");
    }
}