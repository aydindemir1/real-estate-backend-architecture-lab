package com.aydindemir.agent.presentation.error;

import com.aydindemir.agent.application.exception.AgentAlreadyExistsForUserException;
import com.aydindemir.agent.application.exception.AgentConcurrentUpdateException;
import com.aydindemir.agent.application.exception.AgentNotFoundException;
import com.aydindemir.agent.application.exception.DuplicateLicenseNumberException;
import com.aydindemir.agent.domain.exception.InvalidAgencyInfoException;
import com.aydindemir.agent.domain.exception.InvalidAgentStateException;
import com.aydindemir.agent.domain.exception.InvalidLicenseNumberException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class AgentExceptionHandler {

    @ExceptionHandler(AgentNotFoundException.class)
    public ResponseEntity<AgentErrorResponse> handleAgentNotFound(AgentNotFoundException exception) {
        return build(HttpStatus.NOT_FOUND, "AGENT_NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler(AgentAlreadyExistsForUserException.class)
    public ResponseEntity<AgentErrorResponse> handleAgentAlreadyExists(
            AgentAlreadyExistsForUserException exception
    ) {
        return build(HttpStatus.CONFLICT, "AGENT_ALREADY_EXISTS_FOR_USER", exception.getMessage());
    }

    @ExceptionHandler(DuplicateLicenseNumberException.class)
    public ResponseEntity<AgentErrorResponse> handleDuplicateLicense(
            DuplicateLicenseNumberException exception
    ) {
        return build(HttpStatus.CONFLICT, "DUPLICATE_LICENSE_NUMBER", exception.getMessage());
    }

    @ExceptionHandler(InvalidAgentStateException.class)
    public ResponseEntity<AgentErrorResponse> handleInvalidAgentState(
            InvalidAgentStateException exception
    ) {
        return build(HttpStatus.CONFLICT, "INVALID_AGENT_STATE", exception.getMessage());
    }

    @ExceptionHandler(AgentConcurrentUpdateException.class)
    public ResponseEntity<AgentErrorResponse> handleConcurrentUpdate(
            AgentConcurrentUpdateException exception
    ) {
        return build(HttpStatus.CONFLICT, "AGENT_CONCURRENT_UPDATE", exception.getMessage());
    }

    @ExceptionHandler({InvalidLicenseNumberException.class, InvalidAgencyInfoException.class})
    public ResponseEntity<AgentErrorResponse> handleInvalidDomainInput(RuntimeException exception) {
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AgentErrorResponse> handleValidation(
            MethodArgumentNotValidException exception
    ) {
        List<String> fields = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .toList();

        return build(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Request validation failed",
                fields
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<AgentErrorResponse> handleMalformedBody(
            HttpMessageNotReadableException exception
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                "MALFORMED_REQUEST_BODY",
                "Request body could not be read"
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<AgentErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                "TYPE_MISMATCH",
                "Request parameter has an invalid type",
                List.of(exception.getName())
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<AgentErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException exception
    ) {
        return build(
                HttpStatus.CONFLICT,
                "DATA_INTEGRITY_VIOLATION",
                "Data integrity constraint was violated"
        );
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<AgentErrorResponse> handleDataAccess(DataAccessException exception) {
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "DATABASE_ERROR",
                "Unexpected database error"
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<AgentErrorResponse> handleIllegalArgument(
            IllegalArgumentException exception
    ) {
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AgentErrorResponse> handleUnexpected(Exception exception) {
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "Unexpected server error"
        );
    }

    private ResponseEntity<AgentErrorResponse> build(
            HttpStatus status,
            String code,
            String message
    ) {
        return build(status, code, message, null);
    }

    private ResponseEntity<AgentErrorResponse> build(
            HttpStatus status,
            String code,
            String message,
            List<String> fields
    ) {
        AgentErrorResponse body = new AgentErrorResponse(
                code,
                message,
                fields,
                Instant.now(),
                status.name()
        );

        return ResponseEntity.status(status).body(body);
    }

    private String formatFieldError(FieldError fieldError) {
        String message = fieldError.getDefaultMessage() == null
                ? "Invalid value"
                : fieldError.getDefaultMessage();

        return fieldError.getField() + ": " + message;
    }
}
