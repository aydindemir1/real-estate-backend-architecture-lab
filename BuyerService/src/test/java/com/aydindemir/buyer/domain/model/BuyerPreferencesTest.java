package com.aydindemir.buyer.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BuyerPreferencesTest {

    private static final Instant CREATED_AT = Instant.parse("2026-09-29T15:00:00Z");
    private static final Clock CREATE_CLOCK = Clock.fixed(CREATED_AT, ZoneOffset.UTC);
    private static final Instant UPDATED_AT = Instant.parse("2026-09-29T15:10:00Z");
    private static final Clock UPDATE_CLOCK = Clock.fixed(UPDATED_AT, ZoneOffset.UTC);

    @Test
    void createShouldInitializeEmptySavedSearchesAndTimestamps() {
        BuyerPreferences preferences = newPreferences();

        assertThat(preferences.savedSearches()).isEmpty();
        assertThat(preferences.createdAt()).isEqualTo(CREATED_AT);
        assertThat(preferences.updatedAt()).isEqualTo(CREATED_AT);
    }

    @Test
    void createShouldDefensivelyCopyCollections() {
        List<LocationPreference> locations = new ArrayList<>(
                List.of(new LocationPreference("Istanbul", "Kadikoy"))
        );
        Set<String> propertyTypes = new HashSet<>(Set.of("APARTMENT"));
        Set<String> features = new HashSet<>(Set.of("BALCONY"));

        BuyerPreferences preferences = BuyerPreferences.create(
                BuyerId.of(UUID.randomUUID()),
                priceRange(),
                locations,
                propertyTypes,
                new RoomRange(1, 3),
                new AreaRange(new BigDecimal("70"), new BigDecimal("140")),
                features,
                notifications(),
                CREATE_CLOCK
        );

        locations.clear();
        propertyTypes.clear();
        features.clear();

        assertThat(preferences.preferredLocations()).hasSize(1);
        assertThat(preferences.propertyTypes()).containsExactly("APARTMENT");
        assertThat(preferences.preferredFeatures()).containsExactly("BALCONY");

        assertThatThrownBy(() -> preferences.preferredLocations().add(
                new LocationPreference("Ankara", null)
        )).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void updatePreferencesShouldReplaceStateAndTouchUpdatedAt() {
        BuyerPreferences preferences = newPreferences();

        preferences.updatePreferences(
                new PriceRange(
                        new BigDecimal("2000000"),
                        new BigDecimal("7000000"),
                        Currency.getInstance("TRY")
                ),
                List.of(new LocationPreference("Ankara", "Cankaya")),
                Set.of("VILLA"),
                new RoomRange(2, 5),
                new AreaRange(new BigDecimal("100"), new BigDecimal("250")),
                Set.of("GARAGE"),
                new NotificationSettings(false, true, false),
                UPDATE_CLOCK
        );

        assertThat(preferences.priceRange().min()).isEqualByComparingTo("2000000");
        assertThat(preferences.preferredLocations())
                .containsExactly(new LocationPreference("Ankara", "Cankaya"));
        assertThat(preferences.propertyTypes()).containsExactly("VILLA");
        assertThat(preferences.preferredFeatures()).containsExactly("GARAGE");
        assertThat(preferences.notificationSettings().pushEnabled()).isTrue();
        assertThat(preferences.updatedAt()).isEqualTo(UPDATED_AT);
        assertThat(preferences.createdAt()).isEqualTo(CREATED_AT);
    }

    @Test
    void addSavedSearchShouldAppendSearchAndTouchUpdatedAt() {
        BuyerPreferences preferences = newPreferences();
        SavedSearch savedSearch = savedSearch(UUID.randomUUID());

        preferences.addSavedSearch(savedSearch, UPDATE_CLOCK);

        assertThat(preferences.savedSearches()).containsExactly(savedSearch);
        assertThat(preferences.updatedAt()).isEqualTo(UPDATED_AT);
    }

    @Test
    void addSavedSearchShouldRejectDuplicateId() {
        BuyerPreferences preferences = newPreferences();
        UUID id = UUID.randomUUID();

        preferences.addSavedSearch(savedSearch(id), UPDATE_CLOCK);

        assertThatThrownBy(() ->
                preferences.addSavedSearch(
                        savedSearch(id),
                        Clock.fixed(Instant.parse("2026-09-29T15:20:00Z"), ZoneOffset.UTC)
                ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void reconstituteShouldRejectUpdatedAtBeforeCreatedAt() {
        assertThatThrownBy(() -> BuyerPreferences.reconstitute(
                BuyerId.of(UUID.randomUUID()),
                priceRange(),
                List.of(),
                Set.of(),
                null,
                null,
                Set.of(),
                notifications(),
                List.of(),
                CREATED_AT,
                CREATED_AT.minusSeconds(1)
        )).isInstanceOf(IllegalArgumentException.class);
    }

    private BuyerPreferences newPreferences() {
        return BuyerPreferences.create(
                BuyerId.of(UUID.randomUUID()),
                priceRange(),
                List.of(new LocationPreference("Istanbul", "Kadikoy")),
                Set.of("APARTMENT"),
                new RoomRange(1, 3),
                new AreaRange(new BigDecimal("70"), new BigDecimal("140")),
                Set.of("BALCONY"),
                notifications(),
                CREATE_CLOCK
        );
    }

    private PriceRange priceRange() {
        return new PriceRange(
                new BigDecimal("1000000"),
                new BigDecimal("5000000"),
                Currency.getInstance("TRY")
        );
    }

    private NotificationSettings notifications() {
        return new NotificationSettings(true, false, false);
    }

    private SavedSearch savedSearch(UUID id) {
        return new SavedSearch(
                id,
                "Kadikoy apartments",
                priceRange(),
                List.of(new LocationPreference("Istanbul", "Kadikoy")),
                Set.of("APARTMENT"),
                new RoomRange(1, 3),
                new AreaRange(new BigDecimal("70"), new BigDecimal("140")),
                Set.of("BALCONY"),
                CREATED_AT
        );
    }
}
