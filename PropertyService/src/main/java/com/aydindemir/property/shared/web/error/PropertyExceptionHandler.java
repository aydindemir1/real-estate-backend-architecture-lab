package com.aydindemir.property.shared.web.error;

import com.aydindemir.property.shared.domain.exception.InvalidPropertyStateException;
import com.aydindemir.property.shared.domain.exception.PropertyConcurrentModificationException;
import com.aydindemir.property.shared.domain.exception.PropertyNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PropertyExceptionHandler {

    @ExceptionHandler(PropertyNotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(PropertyNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("PROPERTY_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(InvalidPropertyStateException.class)
    ResponseEntity<ApiError> handleInvalidState(InvalidPropertyStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError("INVALID_PROPERTY_STATE", ex.getMessage()));
    }

    @ExceptionHandler(PropertyConcurrentModificationException.class)
    ResponseEntity<ApiError> handleConcurrentModification(PropertyConcurrentModificationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError("PROPERTY_CONCURRENT_MODIFICATION", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("MALFORMED_REQUEST", ex.getMessage()));
    }
}
