package com.aydindemir.seller.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public record PropertyDraftData(
        String title,
        String description,
        String propertyType,
        String city,
        String district,
        String addressLine,
        BigDecimal priceAmount,
        String currency,
        BigDecimal area,
        int roomCount) {

    public PropertyDraftData {
        title = requireText(title, "title");
        description = requireText(description, "description");
        propertyType = requireText(propertyType, "propertyType");
        city = requireText(city, "city");
        district = requireText(district, "district");
        addressLine = requireText(addressLine, "addressLine");
        currency = requireText(currency, "currency").toUpperCase();

        Objects.requireNonNull(priceAmount, "priceAmount must not be null");
        Objects.requireNonNull(area, "area must not be null");

        if (priceAmount.signum() <= 0) {
            throw new IllegalArgumentException("priceAmount must be greater than zero");
        }

        if (area.signum() <= 0) {
            throw new IllegalArgumentException("area must be greater than zero");
        }

        if (roomCount < 0) {
            throw new IllegalArgumentException("roomCount must not be negative");
        }
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");

        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }

        return normalized;
    }
}
