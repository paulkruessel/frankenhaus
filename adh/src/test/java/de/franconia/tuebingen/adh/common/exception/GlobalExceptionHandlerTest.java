package de.franconia.tuebingen.adh.common.exception;

import de.franconia.tuebingen.adh.auth.EmailAlreadyExistsException;
import de.franconia.tuebingen.adh.auth.InvalidRefreshTokenException;
import de.franconia.tuebingen.adh.corps.CorpsAlreadyExistsException;
import de.franconia.tuebingen.adh.corps.CorpsMembershipAlreadyExistsException;
import de.franconia.tuebingen.adh.corps.CorpsMembershipNotFoundException;
import de.franconia.tuebingen.adh.corps.CorpsNotFoundException;
import de.franconia.tuebingen.adh.corps.InvalidLeibburschException;
import de.franconia.tuebingen.adh.corps.InvalidMembershipDataException;
import de.franconia.tuebingen.adh.member.MemberNotFoundException;
import de.franconia.tuebingen.adh.privacy.InvalidPrivacyRuleException;
import de.franconia.tuebingen.adh.privacy.PrivacyResourceNotFoundException;
import de.franconia.tuebingen.adh.profile.AddressNotFoundException;
import de.franconia.tuebingen.adh.profile.ProfileNotFoundException;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void mapsAuthenticationErrorsToDocumentedStatuses() {
        assertStatus(HttpStatus.CONFLICT, handler.emailAlreadyExists(new EmailAlreadyExistsException()));
        assertStatus(HttpStatus.UNAUTHORIZED, handler.invalidCredentials());
        assertStatus(HttpStatus.FORBIDDEN, handler.accountDisabled());
        assertStatus(HttpStatus.UNAUTHORIZED, handler.invalidRefreshToken(new InvalidRefreshTokenException()));
    }

    @Test
    void mapsNotFoundErrorsTo404() {
        assertStatus(HttpStatus.NOT_FOUND, handler.profileNotFound(new ProfileNotFoundException()));
        assertStatus(HttpStatus.NOT_FOUND, handler.addressNotFound(new AddressNotFoundException()));
        assertStatus(HttpStatus.NOT_FOUND, handler.corpsNotFound(new CorpsNotFoundException()));
        assertStatus(HttpStatus.NOT_FOUND, handler.corpsNotFound(new CorpsMembershipNotFoundException()));
        assertStatus(HttpStatus.NOT_FOUND, handler.memberNotFound(new MemberNotFoundException()));
        assertStatus(HttpStatus.NOT_FOUND, handler.privacyResourceNotFound(new PrivacyResourceNotFoundException()));
    }

    @Test
    void mapsConflictAndDomainValidationErrors() {
        assertStatus(HttpStatus.CONFLICT, handler.corpsConflict(new CorpsAlreadyExistsException()));
        assertStatus(HttpStatus.CONFLICT, handler.corpsConflict(new CorpsMembershipAlreadyExistsException()));
        assertStatus(HttpStatus.BAD_REQUEST, handler.invalidCorpsData(new InvalidLeibburschException("invalid")));
        assertStatus(HttpStatus.BAD_REQUEST, handler.invalidCorpsData(new InvalidMembershipDataException("invalid")));
        assertStatus(HttpStatus.BAD_REQUEST, handler.invalidPrivacyRule(new InvalidPrivacyRuleException("invalid")));
    }

    private void assertStatus(HttpStatus expected, org.springframework.http.ResponseEntity<ApiError> response) {
        assertEquals(expected.value(), response.getStatusCode().value());
        assertEquals(expected.value(), response.getBody().status());
    }
}