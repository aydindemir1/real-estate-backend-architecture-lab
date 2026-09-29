package com.aydindemir.buyer.adapter.out.persistence.couchbase;

import com.aydindemir.buyer.adapter.out.persistence.couchbase.adapter.CouchbaseBuyerPreferencesAdapter;
import com.aydindemir.buyer.adapter.out.persistence.couchbase.document.BuyerPreferencesDocument;
import com.aydindemir.buyer.adapter.out.persistence.couchbase.repository.SpringDataBuyerPreferencesRepository;
import com.aydindemir.buyer.domain.model.AreaRange;
import com.aydindemir.buyer.domain.model.BuyerId;
import com.aydindemir.buyer.domain.model.BuyerPreferences;
import com.aydindemir.buyer.domain.model.LocationPreference;
import com.aydindemir.buyer.domain.model.NotificationSettings;
import com.aydindemir.buyer.domain.model.PriceRange;
import com.aydindemir.buyer.domain.model.RoomRange;
import com.aydindemir.buyer.domain.model.SavedSearch;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CouchbaseBuyerPreferencesAdapterIntegrationTest
        extends BuyerCouchbaseContainerTestBase {

    private static final Instant CREATED_AT = Instant.parse("2026-09-29T17:00:00Z");
    private static final Clock CREATE_CLOCK = Clock.fixed(CREATED_AT, ZoneOffset.UTC);
    private static final Clock UPDATE_CLOCK = Clock.fixed(
            Instant.parse("2026-09-29T17:10:00Z"),
            ZoneOffset.UTC
    );

    @Autowired
    private CouchbaseBuyerPreferencesAdapter adapter;

    @Autowired
    private SpringDataBuyerPreferencesRepository repository;

    @Test
    void saveAndLoadShouldRoundTripNestedPreferences() {
        BuyerPreferences preferences = newPreferences(BuyerId.of(UUID.randomUUID()));

        BuyerPreferences saved = adapter.save(preferences);
        BuyerPreferences loaded = adapter.load(preferences.buyerId()).orElseThrow();

        assertThat(saved.buyerId()).isEqualTo(preferences.buyerId());
        assertThat(loaded.buyerId()).isEqualTo(preferences.buyerId());
        assertThat(loaded.priceRange()).isEqualTo(preferences.priceRange());
        assertThat(loaded.preferredLocations()).isEqualTo(preferences.preferredLocations());
        assertThat(loaded.propertyTypes()).isEqualTo(preferences.propertyTypes());
        assertThat(loaded.roomRange()).isEqualTo(preferences.roomRange());
        assertThat(loaded.areaRange()).isEqualTo(preferences.areaRange());
        assertThat(loaded.preferredFeatures()).isEqualTo(preferences.preferredFeatures());
        assertThat(loaded.notificationSettings()).isEqualTo(preferences.notificationSettings());
        assertThat(loaded.createdAt()).isEqualTo(preferences.createdAt());
    }

    @Test
    void saveShouldUseDeterministicDocumentKey() {
        BuyerId buyerId = BuyerId.of(UUID.randomUUID());
        adapter.save(newPreferences(buyerId));

        String expectedDocumentId = BuyerPreferencesDocument.documentId(
                buyerId.value().toString()
        );

        assertThat(repository.findById(expectedDocumentId)).isPresent();
    }

    @Test
    void secondSaveShouldUpdateSameDocument() {
        BuyerId buyerId = BuyerId.of(UUID.randomUUID());
        BuyerPreferences preferences = newPreferences(buyerId);

        adapter.save(preferences);

        preferences.updatePreferences(
                new PriceRange(
                        new BigDecimal("2000000"),
                        new BigDecimal("8000000"),
                        Currency.getInstance("TRY")
                ),
                List.of(new LocationPreference("Ankara", "Cankaya")),
                Set.of("VILLA"),
                new RoomRange(2, 5),
                new AreaRange(new BigDecimal("120"), new BigDecimal("300")),
                Set.of("GARAGE"),
                new NotificationSettings(false, true, false),
                UPDATE_CLOCK
        );

        adapter.save(preferences);

        String documentId = BuyerPreferencesDocument.documentId(
                buyerId.value().toString()
        );
        var persisted = repository.findById(documentId).orElseThrow();

        assertThat(persisted.id()).isEqualTo(documentId);
        assertThat(persisted.priceRange().min()).isEqualByComparingTo("2000000");
        assertThat(persisted.propertyTypes()).containsExactly("VILLA");
        assertThat(persisted.preferredLocations())
                .extracting(location -> location.city())
                .containsExactly("Ankara");
    }

    @Test
    void savedSearchShouldRoundTripInsideBuyerPreferencesDocument() {
        BuyerId buyerId = BuyerId.of(UUID.randomUUID());
        BuyerPreferences preferences = newPreferences(buyerId);

        SavedSearch search = new SavedSearch(
                UUID.randomUUID(),
                "Kadikoy apartments",
                new PriceRange(
                        new BigDecimal("1500000"),
                        new BigDecimal("4500000"),
                        Currency.getInstance("TRY")
                ),
                List.of(new LocationPreference("Istanbul", "Kadikoy")),
                Set.of("APARTMENT"),
                new RoomRange(1, 3),
                new AreaRange(new BigDecimal("70"), new BigDecimal("140")),
                Set.of("BALCONY", "ELEVATOR"),
                CREATED_AT
        );

        preferences.addSavedSearch(search, UPDATE_CLOCK);
        adapter.save(preferences);

        BuyerPreferences loaded = adapter.load(buyerId).orElseThrow();

        assertThat(loaded.savedSearches()).containsExactly(search);
    }

    @Test
    void loadShouldReturnEmptyWhenDocumentDoesNotExist() {
        BuyerId missingBuyerId = BuyerId.of(UUID.randomUUID());

        assertThat(adapter.load(missingBuyerId)).isEmpty();
    }

    private BuyerPreferences newPreferences(BuyerId buyerId) {
        return BuyerPreferences.create(
                buyerId,
                new PriceRange(
                        new BigDecimal("1000000"),
                        new BigDecimal("5000000"),
                        Currency.getInstance("TRY")
                ),
                List.of(new LocationPreference("Istanbul", "Kadikoy")),
                Set.of("APARTMENT"),
                new RoomRange(1, 4),
                new AreaRange(new BigDecimal("70"), new BigDecimal("180")),
                Set.of("BALCONY", "ELEVATOR"),
                new NotificationSettings(true, false, false),
                CREATE_CLOCK
        );
    }
}
