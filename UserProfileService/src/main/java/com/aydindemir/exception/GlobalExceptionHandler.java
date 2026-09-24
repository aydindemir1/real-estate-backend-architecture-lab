package com.aydindemir.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception) {
        log.debug("Malformed request body", exception);
        return buildResponse(ErrorType.MALFORMED_REQUEST_BODY);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorMessage> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException exception) {
        return buildResponse(ErrorType.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorMessage> handleNoResourceFoundException(
            NoResourceFoundException exception) {
        return buildResponse(ErrorType.RESOURCE_NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorMessage> handleDataIntegrityViolationException(
            DataIntegrityViolationException exception) {
        log.warn("Data integrity violation", exception);
        return buildResponse(ErrorType.DATA_INTEGRITY_VIOLATION);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorMessage> handleDataAccessException(DataAccessException exception) {
        log.error("Database access error", exception);
        return buildResponse(ErrorType.DATABASE_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> handleIllegalArgumentException(
            IllegalArgumentException exception) {
        log.warn("Illegal argument: {}", exception.getMessage());
        return buildResponse(ErrorType.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleException(Exception exception) {
        log.error("Unhandled exception", exception);
        return buildResponse(ErrorType.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorMessage> buildResponse(ErrorType errorType) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .code(errorType.getCode())
                .message(errorType.getMessage())
                .status(errorType.getHttpStatus())
                .build();

        return ResponseEntity.status(errorType.getHttpStatus()).body(errorMessage);
    }
}
