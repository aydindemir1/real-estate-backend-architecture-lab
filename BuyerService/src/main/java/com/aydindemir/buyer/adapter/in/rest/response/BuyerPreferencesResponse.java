package com.aydindemir.buyer.adapter.in.rest.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record BuyerPreferencesResponse(
        UUID buyerId,
        MoneyRangeResponse priceRange,
        List<LocationResponse> preferredLocations,
        Set<String> propertyTypes,
        RangeResponse<Integer> roomRange,
        RangeResponse<BigDecimal> areaRange,
        Set<String> preferredFeatures,
        NotificationSettingsResponse notificationSettings,
        List<SavedSearchResponse> savedSearches,
        Instant createdAt,
        Instant updatedAt
) {

    public BuyerPreferencesResponse {
        preferredLocations = preferredLocations == null ? List.of() : List.copyOf(preferredLocations);
        propertyTypes = propertyTypes == null ? Set.of() : Set.copyOf(propertyTypes);
        preferredFeatures = preferredFeatures == null ? Set.of() : Set.copyOf(preferredFeatures);
        savedSearches = savedSearches == null ? List.of() : List.copyOf(savedSearches);
    }

    public record MoneyRangeResponse(
            BigDecimal min,
            BigDecimal max,
            String currency
    ) {
    }

    public record LocationResponse(
            String city,
            String district
    ) {
    }

    public record RangeResponse<T>(
            T min,
            T max
    ) {
    }

    public record NotificationSettingsResponse(
            boolean emailEnabled,
            boolean pushEnabled,
            boolean smsEnabled
    ) {
    }

    public record SavedSearchResponse(
            UUID id,
            String name,
            MoneyRangeResponse priceRange,
            List<LocationResponse> locations,
            Set<String> propertyTypes,
            RangeResponse<Integer> roomRange,
            RangeResponse<BigDecimal> areaRange,
            Set<String> preferredFeatures,
            Instant createdAt
    ) {

        public SavedSearchResponse {
            locations = locations == null ? List.of() : List.copyOf(locations);
            propertyTypes = propertyTypes == null ? Set.of() : Set.copyOf(propertyTypes);
            preferredFeatures = preferredFeatures == null ? Set.of() : Set.copyOf(preferredFeatures);
        }
    }
}
