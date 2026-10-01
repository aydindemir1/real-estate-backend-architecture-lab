package com.aydindemir.property.shared.domain.model;

public record Address(String city, String district, String line1) {
    public Address {
        city = requireText(city, "city");
        district = requireText(district, "district");
        line1 = requireText(line1, "line1");
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value.trim();
    }
}
