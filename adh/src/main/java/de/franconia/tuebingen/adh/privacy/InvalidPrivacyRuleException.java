package de.franconia.tuebingen.adh.privacy;

public class InvalidPrivacyRuleException
        extends RuntimeException {

    public InvalidPrivacyRuleException(
            String message
    ) {
        super(message);
    }
}