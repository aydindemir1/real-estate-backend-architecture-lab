package com.aydindemir.buyer.adapter.in.rest.error;

import com.aydindemir.buyer.application.exception.BuyerPreferencesNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.MDC;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@RestControllerAdvice
public class BuyerApiExceptionHandler {

    private static final String CORRELATION_HEADER = "X-Correlation-Id";

    private final Clock clock;

    public BuyerApiExceptionHandler(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "Clock must not be null");
    }

    @ExceptionHandler(BuyerPreferencesNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            BuyerPreferencesNotFoundException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.NOT_FOUND,
                "BUYER_PREFERENCES_NOT_FOUND",
                exception.getMessage(),
                request,
                List.of()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<ApiErrorResponse.FieldErrorDetail> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ApiErrorResponse.FieldErrorDetail(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .toList();

        return response(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Request validation failed",
                request,
                errors
        );
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            ConstraintViolationException.class
    })
    public ResponseEntity<ApiErrorResponse> handleMalformedRequest(
            Exception exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.BAD_REQUEST,
                "INVALID_REQUEST",
                "Request is malformed or contains an invalid value",
                request,
                List.of()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleSemanticValidation(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "INVALID_BUYER_PREFERENCES",
                exception.getMessage(),
                request,
                List.of()
        );
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiErrorResponse> handlePersistenceFailure(
            DataAccessException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.SERVICE_UNAVAILABLE,
                "BUYER_PERSISTENCE_UNAVAILABLE",
                "Buyer persistence is temporarily unavailable",
                request,
                List.of()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(
            Exception exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                "An unexpected error occurred",
                request,
                List.of()
        );
    }

    private ResponseEntity<ApiErrorResponse> response(
            HttpStatus status,
            String code,
            String message,
            HttpServletRequest request,
            List<ApiErrorResponse.FieldErrorDetail> errors
    ) {
        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(clock),
                status.value(),
                code,
                message,
                request.getRequestURI(),
                correlationId(request),
                traceId(),
                errors
        );

        return ResponseEntity.status(status).body(body);
    }

    private String correlationId(HttpServletRequest request) {
        String correlationId = request.getHeader(CORRELATION_HEADER);
        return correlationId == null || correlationId.isBlank() ? null : correlationId;
    }

    private String traceId() {
        String traceId = MDC.get("traceId");
        return traceId == null || traceId.isBlank() ? null : traceId;
    }
}
