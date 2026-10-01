package com.aydindemir.property.shared.domain.exception;

public class PropertyConcurrentModificationException extends RuntimeException {
    public PropertyConcurrentModificationException(String propertyId, Throwable cause) {
        super("Property was concurrently modified: " + propertyId, cause);
    }
}
