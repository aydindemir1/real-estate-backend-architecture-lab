package com.aydindemir.buyer.domain.model;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record SavedSearch(
        UUID id,
        String name,
        PriceRange priceRange,
        List<LocationPreference> locations,
        Set<String> propertyTypes,
        RoomRange roomRange,
        AreaRange areaRange,
        Set<String> preferredFeatures,
        Instant createdAt
) {

    public SavedSearch {
        Objects.requireNonNull(id, "id must not be null");
        name = normalizeRequired(name, "name");
        locations = immutableList(locations);
        propertyTypes = immutableNormalizedSet(propertyTypes);
        preferredFeatures = immutableNormalizedSet(preferredFeatures);
        Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

    private static String normalizeRequired(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }

    private static <T> List<T> immutableList(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }

    private static Set<String> immutableNormalizedSet(Set<String> values) {
        if (values == null || values.isEmpty()) {
            return Set.of();
        }

        Set<String> normalized = values.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .collect(java.util.stream.Collectors.toUnmodifiableSet());

        return normalized.isEmpty() ? Collections.emptySet() : normalized;
    }
}
