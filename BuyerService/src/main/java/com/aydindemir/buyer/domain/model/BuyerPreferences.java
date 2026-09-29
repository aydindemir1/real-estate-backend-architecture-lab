package com.aydindemir.buyer.domain.model;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class BuyerPreferences {

    private final BuyerId buyerId;
    private PriceRange priceRange;
    private List<LocationPreference> preferredLocations;
    private Set<String> propertyTypes;
    private RoomRange roomRange;
    private AreaRange areaRange;
    private Set<String> preferredFeatures;
    private NotificationSettings notificationSettings;
    private List<SavedSearch> savedSearches;
    private final Instant createdAt;
    private Instant updatedAt;

    private BuyerPreferences(
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
        this.buyerId = Objects.requireNonNull(buyerId, "Buyer id must not be null");
        this.priceRange = Objects.requireNonNull(priceRange, "Price range must not be null");
        this.preferredLocations = immutableList(preferredLocations);
        this.propertyTypes = immutableStringSet(propertyTypes);
        this.roomRange = roomRange;
        this.areaRange = areaRange;
        this.preferredFeatures = immutableStringSet(preferredFeatures);
        this.notificationSettings = Objects.requireNonNull(
                notificationSettings,
                "Notification settings must not be null"
        );
        this.savedSearches = immutableList(savedSearches);
        this.createdAt = Objects.requireNonNull(createdAt, "Created at must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at must not be null");

        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("Updated at must not be before created at");
        }
    }

    public static BuyerPreferences create(
            BuyerId buyerId,
            PriceRange priceRange,
            List<LocationPreference> preferredLocations,
            Set<String> propertyTypes,
            RoomRange roomRange,
            AreaRange areaRange,
            Set<String> preferredFeatures,
            NotificationSettings notificationSettings,
            Clock clock
    ) {
        Objects.requireNonNull(clock, "Clock must not be null");
        Instant now = Instant.now(clock);

        return new BuyerPreferences(
                buyerId,
                priceRange,
                preferredLocations,
                propertyTypes,
                roomRange,
                areaRange,
                preferredFeatures,
                notificationSettings,
                List.of(),
                now,
                now
        );
    }

    public static BuyerPreferences reconstitute(
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
        return new BuyerPreferences(
                buyerId,
                priceRange,
                preferredLocations,
                propertyTypes,
                roomRange,
                areaRange,
                preferredFeatures,
                notificationSettings,
                savedSearches,
                createdAt,
                updatedAt
        );
    }

    public void updatePreferences(
            PriceRange priceRange,
            List<LocationPreference> preferredLocations,
            Set<String> propertyTypes,
            RoomRange roomRange,
            AreaRange areaRange,
            Set<String> preferredFeatures,
            NotificationSettings notificationSettings,
            Clock clock
    ) {
        this.priceRange = Objects.requireNonNull(priceRange, "Price range must not be null");
        this.preferredLocations = immutableList(preferredLocations);
        this.propertyTypes = immutableStringSet(propertyTypes);
        this.roomRange = roomRange;
        this.areaRange = areaRange;
        this.preferredFeatures = immutableStringSet(preferredFeatures);
        this.notificationSettings = Objects.requireNonNull(
                notificationSettings,
                "Notification settings must not be null"
        );
        touch(clock);
    }

    public void addSavedSearch(SavedSearch savedSearch, Clock clock) {
        Objects.requireNonNull(savedSearch, "Saved search must not be null");

        boolean duplicateId = savedSearches.stream()
                .anyMatch(existing -> existing.id().equals(savedSearch.id()));

        if (duplicateId) {
            throw new IllegalArgumentException("Saved search id already exists");
        }

        List<SavedSearch> updatedSearches = new ArrayList<>(savedSearches);
        updatedSearches.add(savedSearch);
        this.savedSearches = List.copyOf(updatedSearches);
        touch(clock);
    }

    private void touch(Clock clock) {
        Objects.requireNonNull(clock, "Clock must not be null");
        updatedAt = Instant.now(clock);
    }

    private static <T> List<T> immutableList(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }

    private static Set<String> immutableStringSet(Set<String> values) {
        if (values == null || values.isEmpty()) {
            return Set.of();
        }

        Set<String> normalized = values.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .collect(java.util.stream.Collectors.toUnmodifiableSet());

        return normalized.isEmpty() ? Set.of() : normalized;
    }

    public BuyerId buyerId() {
        return buyerId;
    }

    public PriceRange priceRange() {
        return priceRange;
    }

    public List<LocationPreference> preferredLocations() {
        return preferredLocations;
    }

    public Set<String> propertyTypes() {
        return propertyTypes;
    }

    public RoomRange roomRange() {
        return roomRange;
    }

    public AreaRange areaRange() {
        return areaRange;
    }

    public Set<String> preferredFeatures() {
        return preferredFeatures;
    }

    public NotificationSettings notificationSettings() {
        return notificationSettings;
    }

    public List<SavedSearch> savedSearches() {
        return savedSearches;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
