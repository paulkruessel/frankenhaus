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
import de.franconia.tuebingen.adh.profile.AddressNotFoundException;
import de.franconia.tuebingen.adh.profile.ProfileNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> emailAlreadyExists(
            EmailAlreadyExistsException exception) {

        return error(
                HttpStatus.CONFLICT,
                exception.getMessage());
    }

    @ExceptionHandler({
            BadCredentialsException.class
    })
    public ResponseEntity<ApiError> invalidCredentials() {

        return error(
                HttpStatus.UNAUTHORIZED,
                "E-Mail oder Passwort ist falsch");
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiError> accountDisabled() {

        return error(
                HttpStatus.FORBIDDEN,
                "Das Benutzerkonto ist deaktiviert");
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiError> invalidRefreshToken(
            InvalidRefreshTokenException exception) {

        return error(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validation(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors = new HashMap<>();

        exception
                .getBindingResult()
                .getFieldErrors()
                .forEach(fieldError ->

                errors.put(
                        fieldError.getField(),
                        fieldError
                                .getDefaultMessage()));

        ApiError body = new ApiError(
                Instant.now(),
                400,
                "Bad Request",
                "Validierung fehlgeschlagen",
                errors);

        return ResponseEntity
                .badRequest()
                .body(body);
    }

    private ResponseEntity<ApiError> error(
            HttpStatus status,
            String message) {

        ApiError body = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                Map.of());

        return ResponseEntity
                .status(status)
                .body(body);
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<ApiError> profileNotFound(
            ProfileNotFoundException exception) {

        return error(
                HttpStatus.NOT_FOUND,
                exception.getMessage());
    }

    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<ApiError> addressNotFound(
            AddressNotFoundException exception) {

        return error(
                HttpStatus.NOT_FOUND,
                exception.getMessage());
    }

    @ExceptionHandler({
            CorpsNotFoundException.class,
            CorpsMembershipNotFoundException.class
    })
    public ResponseEntity<ApiError> corpsNotFound(
            RuntimeException exception) {

        return error(
                HttpStatus.NOT_FOUND,
                exception.getMessage());
    }

    @ExceptionHandler({
            CorpsAlreadyExistsException.class,
            CorpsMembershipAlreadyExistsException.class
    })
    public ResponseEntity<ApiError> corpsConflict(
            RuntimeException exception) {

        return error(
                HttpStatus.CONFLICT,
                exception.getMessage());
    }

    @ExceptionHandler({
            InvalidLeibburschException.class,
            InvalidMembershipDataException.class
    })
    public ResponseEntity<ApiError> invalidCorpsData(
            RuntimeException exception) {

        return error(
                HttpStatus.BAD_REQUEST,
                exception.getMessage());
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ApiError> memberNotFound(
            MemberNotFoundException exception) {

        return error(
                HttpStatus.NOT_FOUND,
                exception.getMessage());
    }
}