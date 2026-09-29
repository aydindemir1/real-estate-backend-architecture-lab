package com.aydindemir.buyer.adapter.in.rest.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record AddSavedSearchRequest(
        @NotBlank String name,
        @PositiveOrZero BigDecimal minPrice,
        @PositiveOrZero BigDecimal maxPrice,
        String currency,
        List<@Valid LocationRequest> locations,
        Set<@NotBlank String> propertyTypes,
        @PositiveOrZero Integer minRooms,
        @PositiveOrZero Integer maxRooms,
        @PositiveOrZero BigDecimal minArea,
        @PositiveOrZero BigDecimal maxArea,
        Set<@NotBlank String> preferredFeatures
) {

    public record LocationRequest(
            @NotBlank String city,
            String district
    ) {
    }
}
