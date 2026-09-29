package com.aydindemir.buyer.adapter.in.rest.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record UpdateBuyerPreferencesRequest(
        @NotNull @PositiveOrZero BigDecimal minPrice,
        @NotNull @PositiveOrZero BigDecimal maxPrice,
        @NotBlank String currency,
        List<@Valid LocationRequest> preferredLocations,
        Set<@NotBlank String> propertyTypes,
        @PositiveOrZero Integer minRooms,
        @PositiveOrZero Integer maxRooms,
        @PositiveOrZero BigDecimal minArea,
        @PositiveOrZero BigDecimal maxArea,
        Set<@NotBlank String> preferredFeatures,
        @NotNull @Valid NotificationSettingsRequest notificationSettings
) {

    public record LocationRequest(
            @NotBlank String city,
            String district
    ) {
    }

    public record NotificationSettingsRequest(
            boolean emailEnabled,
            boolean pushEnabled,
            boolean smsEnabled
    ) {
    }
}
