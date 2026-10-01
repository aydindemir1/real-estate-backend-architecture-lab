package com.aydindemir.property.shared.domain.exception;

public class PropertyNotFoundException extends RuntimeException {
    public PropertyNotFoundException(String propertyId) {
        super("Property not found: " + propertyId);
    }
}
