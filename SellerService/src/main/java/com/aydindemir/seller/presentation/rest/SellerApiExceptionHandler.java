package com.aydindemir.seller.presentation.rest;

import com.aydindemir.seller.domain.exception.InvalidListingSubmissionStateException;
import com.aydindemir.seller.domain.exception.ListingSubmissionNotFoundException;
import com.aydindemir.seller.domain.exception.SellerNotActiveException;
import com.aydindemir.seller.domain.exception.SellerNotFoundException;
import com.aydindemir.seller.presentation.rest.response.ApiErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;
import java.util.List;

@RestControllerAdvice
public class SellerApiExceptionHandler {

    @ExceptionHandler(SellerNotFoundException.class)
    ResponseEntity<ApiErrorResponse> handleSellerNotFound(SellerNotFoundException exception) {
        return response(
                HttpStatus.NOT_FOUND,
                "SELLER_NOT_FOUND",
                "Seller bulunamadı.",
                List.of(exception.getMessage()));
    }

    @ExceptionHandler(ListingSubmissionNotFoundException.class)
    ResponseEntity<ApiErrorResponse> handleListingSubmissionNotFound(
            ListingSubmissionNotFoundException exception) {
        return response(
                HttpStatus.NOT_FOUND,
                "LISTING_SUBMISSION_NOT_FOUND",
                "Listing submission bulunamadı.",
                List.of(exception.getMessage()));
    }

    @ExceptionHandler(SellerNotActiveException.class)
    ResponseEntity<ApiErrorResponse> handleSellerNotActive(SellerNotActiveException exception) {
        return response(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "SELLER_NOT_ACTIVE",
                "Seller listing göndermek için ACTIVE durumda olmalıdır.",
                List.of(exception.getMessage()));
    }

    @ExceptionHandler(InvalidListingSubmissionStateException.class)
    ResponseEntity<ApiErrorResponse> handleInvalidListingSubmissionState(
            InvalidListingSubmissionStateException exception) {
        return response(
                HttpStatus.CONFLICT,
                "INVALID_LISTING_SUBMISSION_STATE",
                "Listing submission mevcut durumundan istenen duruma geçirilemez.",
                List.of(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception) {
        List<String> details = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .toList();

        return response(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "İstek doğrulama kurallarını sağlamıyor.",
                details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException exception) {
        List<String> details = exception.getConstraintViolations()
                .stream()
                .map(this::formatConstraintViolation)
                .toList();

        return response(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "İstek doğrulama kurallarını sağlamıyor.",
                details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiErrorResponse> handleMalformedBody(
            HttpMessageNotReadableException exception) {
        return response(
                HttpStatus.BAD_REQUEST,
                "MALFORMED_REQUEST_BODY",
                "İstek gövdesi okunamadı veya geçersiz JSON içeriyor.",
                List.of());
    }

    @ExceptionHandler({IllegalArgumentException.class, DateTimeParseException.class})
    ResponseEntity<ApiErrorResponse> handleBadRequest(RuntimeException exception) {
        return response(
                HttpStatus.BAD_REQUEST,
                "BAD_REQUEST",
                "Geçersiz istek.",
                List.of(exception.getMessage() == null ? "Invalid request" : exception.getMessage()));
    }

    @ExceptionHandler(DataAccessException.class)
    ResponseEntity<ApiErrorResponse> handleDataAccess(DataAccessException exception) {
        return response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "DATABASE_ERROR",
                "Veritabanı işlemi sırasında beklenmeyen bir hata oluştu.",
                List.of());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiErrorResponse> handleUnexpected(Exception exception) {
        return response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "Beklenmeyen bir sunucu hatası oluştu.",
                List.of());
    }

    private ResponseEntity<ApiErrorResponse> response(
            HttpStatus status,
            String code,
            String message,
            List<String> details) {
        return ResponseEntity.status(status)
                .body(ApiErrorResponse.of(
                        code,
                        message,
                        status.value(),
                        details));
    }

    private String formatFieldError(FieldError fieldError) {
        String message = fieldError.getDefaultMessage() == null
                ? "Geçersiz değer."
                : fieldError.getDefaultMessage();

        return fieldError.getField() + ": " + message;
    }

    private String formatConstraintViolation(ConstraintViolation<?> violation) {
        return violation.getPropertyPath() + ": " + violation.getMessage();
    }
}
