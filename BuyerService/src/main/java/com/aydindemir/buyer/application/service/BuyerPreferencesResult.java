package com.aydindemir.buyer.application.service;

import com.aydindemir.buyer.domain.model.AreaRange;
import com.aydindemir.buyer.domain.model.BuyerId;
import com.aydindemir.buyer.domain.model.LocationPreference;
import com.aydindemir.buyer.domain.model.NotificationSettings;
import com.aydindemir.buyer.domain.model.PriceRange;
import com.aydindemir.buyer.domain.model.RoomRange;
import com.aydindemir.buyer.domain.model.SavedSearch;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public record BuyerPreferencesResult(
        BuyerId buyerId,
        PriceRange priceRange,
        List<LocationPreference> preferredLocations,
        Set<String> propertyTypes,
        RoomRange roomRange,
        AreaRange areaRange,
        Set<String> preferredFeatures,
        NotificationSettings notificationSettings,
        List<SavedSearch> savedSearches,
        Instant createdAt,
        Instant updatedAt
) {

    public BuyerPreferencesResult {
        Objects.requireNonNull(buyerId, "buyerId must not be null");
        Objects.requireNonNull(priceRange, "priceRange must not be null");
        preferredLocations = preferredLocations == null ? List.of() : List.copyOf(preferredLocations);
        propertyTypes = propertyTypes == null ? Set.of() : Set.copyOf(propertyTypes);
        preferredFeatures = preferredFeatures == null ? Set.of() : Set.copyOf(preferredFeatures);
        Objects.requireNonNull(notificationSettings, "notificationSettings must not be null");
        savedSearches = savedSearches == null ? List.of() : List.copyOf(savedSearches);
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }
}
