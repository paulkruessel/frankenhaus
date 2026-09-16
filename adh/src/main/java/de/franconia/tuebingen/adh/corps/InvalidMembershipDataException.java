package de.franconia.tuebingen.adh.corps;

public class InvalidMembershipDataException
        extends RuntimeException {

    public InvalidMembershipDataException(
            String message
    ) {
        super(message);
    }
}