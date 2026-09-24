package com.aydindemir.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthServiceException.class)
    public ResponseEntity<ErrorMessage> handleAuthServiceException(AuthServiceException exception) {
        ErrorType errorType = exception.getType();

        log.warn("Business exception: code={}, type={}, message={}",
                errorType.getCode(), errorType.name(), errorType.getMessage());

        return buildResponse(errorType);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {

        List<String> fields = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .toList();

        return buildResponse(ErrorType.VALIDATION_ERROR, fields);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorMessage> handleBindException(BindException exception) {
        List<String> fields = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .toList();

        return buildResponse(ErrorType.VALIDATION_ERROR, fields);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception) {

        log.debug("Malformed request body", exception);
        return buildResponse(ErrorType.MALFORMED_REQUEST_BODY);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorMessage> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException exception) {

        return buildResponse(
                ErrorType.MISSING_REQUEST_PARAMETER,
                List.of(exception.getParameterName())
        );
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorMessage> handleMissingRequestHeaderException(
            MissingRequestHeaderException exception) {

        return buildResponse(
                ErrorType.MISSING_REQUEST_HEADER,
                List.of(exception.getHeaderName())
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorMessage> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException exception) {

        return buildResponse(
                ErrorType.TYPE_MISMATCH,
                List.of(exception.getName())
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorMessage> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException exception) {

        return buildResponse(ErrorType.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorMessage> handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException exception) {

        return buildResponse(ErrorType.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ErrorMessage> handleHttpMediaTypeNotAcceptableException(
            HttpMediaTypeNotAcceptableException exception) {

        return buildResponse(ErrorType.NOT_ACCEPTABLE);
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

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorMessage> handleRuntimeException(RuntimeException exception) {
        log.error("Unhandled runtime exception", exception);
        return buildResponse(ErrorType.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleException(Exception exception) {
        log.error("Unhandled exception", exception);
        return buildResponse(ErrorType.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorMessage> buildResponse(ErrorType errorType) {
        return buildResponse(errorType, null);
    }

    private ResponseEntity<ErrorMessage> buildResponse(ErrorType errorType, List<String> fields) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .code(errorType.getCode())
                .message(errorType.getMessage())
                .fields(fields)
                .status(errorType.getHttpStatus())
                .build();

        return ResponseEntity.status(errorType.getHttpStatus()).body(errorMessage);
    }

    private String formatFieldError(FieldError fieldError) {
        String message = fieldError.getDefaultMessage() == null
                ? "Geçersiz değer."
                : fieldError.getDefaultMessage();

        return fieldError.getField() + ": " + message;
    }
}
