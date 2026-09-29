package com.aydindemir.buyer.application.service;

import com.aydindemir.buyer.application.exception.BuyerPreferencesNotFoundException;
import com.aydindemir.buyer.application.port.in.AddSavedSearchCommand;
import com.aydindemir.buyer.application.port.in.GetBuyerPreferencesQuery;
import com.aydindemir.buyer.application.port.in.UpdateBuyerPreferencesCommand;
import com.aydindemir.buyer.application.support.InMemoryBuyerPreferencesStore;
import com.aydindemir.buyer.domain.model.AreaRange;
import com.aydindemir.buyer.domain.model.BuyerId;
import com.aydindemir.buyer.domain.model.LocationPreference;
import com.aydindemir.buyer.domain.model.NotificationSettings;
import com.aydindemir.buyer.domain.model.PriceRange;
import com.aydindemir.buyer.domain.model.RoomRange;
import com.aydindemir.buyer.domain.model.SavedSearch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Currency;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BuyerPreferencesApplicationServiceTest {

    private static final Instant NOW = Instant.parse("2026-09-29T16:30:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    private InMemoryBuyerPreferencesStore store;
    private BuyerPreferencesApplicationService service;

    @BeforeEach
    void setUp() {
        store = new InMemoryBuyerPreferencesStore();
        service = new BuyerPreferencesApplicationService(store, store, CLOCK);
    }

    @Test
    void updateShouldCreatePreferencesWhenMissing() {
        BuyerId buyerId = BuyerId.of(UUID.randomUUID());

        BuyerPreferencesResult result = service.update(command(
                buyerId,
                new BigDecimal("1000000"),
                new BigDecimal("5000000"),
                "APARTMENT"
        ));

        assertThat(result.buyerId()).isEqualTo(buyerId);
        assertThat(result.priceRange().min()).isEqualByComparingTo("1000000");
        assertThat(result.savedSearches()).isEmpty();
        assertThat(store.size()).isEqualTo(1);
        assertThat(store.load(buyerId)).isPresent();
    }

    @Test
    void updateShouldModifyExistingPreferencesWithoutCreatingSecondRecord() {
        BuyerId buyerId = BuyerId.of(UUID.randomUUID());
        service.update(command(
                buyerId,
                new BigDecimal("1000000"),
                new BigDecimal("5000000"),
                "APARTMENT"
        ));

        BuyerPreferencesResult updated = service.update(command(
                buyerId,
                new BigDecimal("2000000"),
                new BigDecimal("7000000"),
                "VILLA"
        ));

        assertThat(updated.priceRange().min()).isEqualByComparingTo("2000000");
        assertThat(updated.propertyTypes()).containsExactly("VILLA");
        assertThat(store.size()).isEqualTo(1);
    }

    @Test
    void getShouldReturnPersistedPreferences() {
        BuyerId buyerId = BuyerId.of(UUID.randomUUID());
        service.update(command(
                buyerId,
                new BigDecimal("1000000"),
                new BigDecimal("5000000"),
                "APARTMENT"
        ));

        BuyerPreferencesResult result = service.get(new GetBuyerPreferencesQuery(buyerId));

        assertThat(result.buyerId()).isEqualTo(buyerId);
        assertThat(result.propertyTypes()).containsExactly("APARTMENT");
    }

    @Test
    void getShouldThrowWhenPreferencesAreMissing() {
        BuyerId buyerId = BuyerId.of(UUID.randomUUID());

        assertThatThrownBy(() ->
                service.get(new GetBuyerPreferencesQuery(buyerId)))
                .isInstanceOf(BuyerPreferencesNotFoundException.class)
                .hasMessageContaining(buyerId.value().toString());
    }

    @Test
    void addSavedSearchShouldPersistUpdatedAggregate() {
        BuyerId buyerId = BuyerId.of(UUID.randomUUID());
        service.update(command(
                buyerId,
                new BigDecimal("1000000"),
                new BigDecimal("5000000"),
                "APARTMENT"
        ));

        SavedSearch search = new SavedSearch(
                UUID.randomUUID(),
                "Kadikoy apartments",
                priceRange(new BigDecimal("1500000"), new BigDecimal("4500000")),
                List.of(new LocationPreference("Istanbul", "Kadikoy")),
                Set.of("APARTMENT"),
                new RoomRange(1, 3),
                new AreaRange(new BigDecimal("70"), new BigDecimal("140")),
                Set.of("BALCONY"),
                NOW
        );

        BuyerPreferencesResult result = service.add(
                new AddSavedSearchCommand(buyerId, search)
        );

        assertThat(result.savedSearches()).containsExactly(search);
        assertThat(store.load(buyerId))
                .get()
                .extracting(preferences -> preferences.savedSearches().size())
                .isEqualTo(1);
    }

    @Test
    void addSavedSearchShouldThrowWhenPreferencesAreMissing() {
        BuyerId buyerId = BuyerId.of(UUID.randomUUID());

        SavedSearch search = new SavedSearch(
                UUID.randomUUID(),
                "Any search",
                null,
                List.of(),
                Set.of(),
                null,
                null,
                Set.of(),
                NOW
        );

        assertThatThrownBy(() ->
                service.add(new AddSavedSearchCommand(buyerId, search)))
                .isInstanceOf(BuyerPreferencesNotFoundException.class);
    }

    @Test
    void invalidSemanticRangeShouldBeRejectedBeforePersistence() {
        BuyerId buyerId = BuyerId.of(UUID.randomUUID());

        assertThatThrownBy(() -> new UpdateBuyerPreferencesCommand(
                buyerId,
                new PriceRange(
                        new BigDecimal("5000000"),
                        new BigDecimal("1000000"),
                        Currency.getInstance("TRY")
                ),
                List.of(),
                Set.of(),
                null,
                null,
                Set.of(),
                new NotificationSettings(true, false, false)
        )).isInstanceOf(IllegalArgumentException.class);

        assertThat(store.size()).isZero();
    }

    private UpdateBuyerPreferencesCommand command(
            BuyerId buyerId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String propertyType
    ) {
        return new UpdateBuyerPreferencesCommand(
                buyerId,
                priceRange(minPrice, maxPrice),
                List.of(new LocationPreference("Istanbul", "Kadikoy")),
                Set.of(propertyType),
                new RoomRange(1, 4),
                new AreaRange(new BigDecimal("70"), new BigDecimal("180")),
                Set.of("BALCONY"),
                new NotificationSettings(true, false, false)
        );
    }

    private PriceRange priceRange(BigDecimal min, BigDecimal max) {
        return new PriceRange(min, max, Currency.getInstance("TRY"));
    }
}
