package com.aydindemir.buyer.adapter.out.persistence.couchbase.document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

public record SavedSearchDocument(
        String id,
        String name,
        MoneyRangeDocument priceRange,
        List<LocationPreferenceDocument> locations,
        Set<String> propertyTypes,
        Integer minRooms,
        Integer maxRooms,
        BigDecimal minArea,
        BigDecimal maxArea,
        Set<String> preferredFeatures,
        Instant createdAt
) {

    public SavedSearchDocument {
        locations = locations == null ? List.of() : List.copyOf(locations);
        propertyTypes = propertyTypes == null ? Set.of() : Set.copyOf(propertyTypes);
        preferredFeatures = preferredFeatures == null ? Set.of() : Set.copyOf(preferredFeatures);
    }
}
