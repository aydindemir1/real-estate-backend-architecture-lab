package com.aydindemir.buyer.domain.model;

import java.util.Objects;

public record LocationPreference(String city, String district) {

    public LocationPreference {
        city = normalizeRequired(city, "city");
        district = normalizeOptional(district);
    }

    private static String normalizeRequired(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }

    private static String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
