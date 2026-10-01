package com.aydindemir.property.shared.domain.exception;

public class InvalidPropertyStateException extends RuntimeException {
    public InvalidPropertyStateException(String message) {
        super(message);
    }
}
