package com.aydindemir.buyer.adapter.out.persistence.couchbase.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Document
public record BuyerPreferencesDocument(
        @Id String id,
        String type,
        String buyerId,
        MoneyRangeDocument priceRange,
        List<LocationPreferenceDocument> preferredLocations,
        Set<String> propertyTypes,
        Integer minRooms,
        Integer maxRooms,
        BigDecimal minArea,
        BigDecimal maxArea,
        Set<String> preferredFeatures,
        NotificationSettingsDocument notificationSettings,
        List<SavedSearchDocument> savedSearches,
        Instant createdAt,
        Instant updatedAt
) {

    public static final String DOCUMENT_TYPE = "buyer-preferences";
    public static final String KEY_PREFIX = "buyer-preferences::";

    public BuyerPreferencesDocument {
        preferredLocations = preferredLocations == null ? List.of() : List.copyOf(preferredLocations);
        propertyTypes = propertyTypes == null ? Set.of() : Set.copyOf(propertyTypes);
        preferredFeatures = preferredFeatures == null ? Set.of() : Set.copyOf(preferredFeatures);
        savedSearches = savedSearches == null ? List.of() : List.copyOf(savedSearches);
    }

    public static String documentId(String buyerId) {
        if (buyerId == null || buyerId.isBlank()) {
            throw new IllegalArgumentException("buyerId must not be blank");
        }
        return KEY_PREFIX + buyerId;
    }
}
