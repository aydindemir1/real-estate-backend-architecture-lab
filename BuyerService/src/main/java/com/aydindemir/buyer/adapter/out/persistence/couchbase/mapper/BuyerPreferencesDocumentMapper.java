package com.aydindemir.buyer.adapter.out.persistence.couchbase.mapper;

import com.aydindemir.buyer.adapter.out.persistence.couchbase.document.BuyerPreferencesDocument;
import com.aydindemir.buyer.adapter.out.persistence.couchbase.document.LocationPreferenceDocument;
import com.aydindemir.buyer.adapter.out.persistence.couchbase.document.MoneyRangeDocument;
import com.aydindemir.buyer.adapter.out.persistence.couchbase.document.NotificationSettingsDocument;
import com.aydindemir.buyer.adapter.out.persistence.couchbase.document.SavedSearchDocument;
import com.aydindemir.buyer.domain.model.AreaRange;
import com.aydindemir.buyer.domain.model.BuyerId;
import com.aydindemir.buyer.domain.model.BuyerPreferences;
import com.aydindemir.buyer.domain.model.LocationPreference;
import com.aydindemir.buyer.domain.model.NotificationSettings;
import com.aydindemir.buyer.domain.model.PriceRange;
import com.aydindemir.buyer.domain.model.RoomRange;
import com.aydindemir.buyer.domain.model.SavedSearch;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class BuyerPreferencesDocumentMapper {

    public BuyerPreferencesDocument toDocument(BuyerPreferences preferences) {
        String buyerId = preferences.buyerId().value().toString();

        return new BuyerPreferencesDocument(
                BuyerPreferencesDocument.documentId(buyerId),
                BuyerPreferencesDocument.DOCUMENT_TYPE,
                buyerId,
                toMoneyRange(preferences.priceRange()),
                preferences.preferredLocations().stream().map(this::toLocation).toList(),
                preferences.propertyTypes(),
                preferences.roomRange() == null ? null : preferences.roomRange().min(),
                preferences.roomRange() == null ? null : preferences.roomRange().max(),
                preferences.areaRange() == null ? null : preferences.areaRange().min(),
                preferences.areaRange() == null ? null : preferences.areaRange().max(),
                preferences.preferredFeatures(),
                toNotificationSettings(preferences.notificationSettings()),
                preferences.savedSearches().stream().map(this::toSavedSearch).toList(),
                preferences.createdAt(),
                preferences.updatedAt()
        );
    }

    public BuyerPreferences toDomain(BuyerPreferencesDocument document) {
        return BuyerPreferences.reconstitute(
                BuyerId.of(UUID.fromString(document.buyerId())),
                toPriceRange(document.priceRange()),
                document.preferredLocations().stream().map(this::toLocation).toList(),
                document.propertyTypes(),
                toRoomRange(document.minRooms(), document.maxRooms()),
                toAreaRange(document.minArea(), document.maxArea()),
                document.preferredFeatures(),
                toNotificationSettings(document.notificationSettings()),
                document.savedSearches().stream().map(this::toSavedSearch).toList(),
                document.createdAt(),
                document.updatedAt()
        );
    }

    private MoneyRangeDocument toMoneyRange(PriceRange range) {
        return new MoneyRangeDocument(
                range.min(),
                range.max(),
                range.currency().getCurrencyCode()
        );
    }

    private PriceRange toPriceRange(MoneyRangeDocument range) {
        return new PriceRange(
                range.min(),
                range.max(),
                Currency.getInstance(range.currency())
        );
    }

    private LocationPreferenceDocument toLocation(LocationPreference location) {
        return new LocationPreferenceDocument(location.city(), location.district());
    }

    private LocationPreference toLocation(LocationPreferenceDocument location) {
        return new LocationPreference(location.city(), location.district());
    }

    private NotificationSettingsDocument toNotificationSettings(NotificationSettings settings) {
        return new NotificationSettingsDocument(
                settings.emailEnabled(),
                settings.pushEnabled(),
                settings.smsEnabled()
        );
    }

    private NotificationSettings toNotificationSettings(NotificationSettingsDocument settings) {
        return new NotificationSettings(
                settings.emailEnabled(),
                settings.pushEnabled(),
                settings.smsEnabled()
        );
    }

    private SavedSearchDocument toSavedSearch(SavedSearch search) {
        return new SavedSearchDocument(
                search.id().toString(),
                search.name(),
                search.priceRange() == null ? null : toMoneyRange(search.priceRange()),
                search.locations().stream().map(this::toLocation).toList(),
                search.propertyTypes(),
                search.roomRange() == null ? null : search.roomRange().min(),
                search.roomRange() == null ? null : search.roomRange().max(),
                search.areaRange() == null ? null : search.areaRange().min(),
                search.areaRange() == null ? null : search.areaRange().max(),
                search.preferredFeatures(),
                search.createdAt()
        );
    }

    private SavedSearch toSavedSearch(SavedSearchDocument search) {
        return new SavedSearch(
                UUID.fromString(search.id()),
                search.name(),
                search.priceRange() == null ? null : toPriceRange(search.priceRange()),
                search.locations().stream().map(this::toLocation).toList(),
                search.propertyTypes(),
                toRoomRange(search.minRooms(), search.maxRooms()),
                toAreaRange(search.minArea(), search.maxArea()),
                search.preferredFeatures(),
                search.createdAt()
        );
    }

    private RoomRange toRoomRange(Integer min, Integer max) {
        if (min == null && max == null) {
            return null;
        }
        if (min == null || max == null) {
            throw new IllegalArgumentException("Room range persistence data is incomplete");
        }
        return new RoomRange(min, max);
    }

    private AreaRange toAreaRange(BigDecimal min, BigDecimal max) {
        if (min == null && max == null) {
            return null;
        }
        if (min == null || max == null) {
            throw new IllegalArgumentException("Area range persistence data is incomplete");
        }
        return new AreaRange(min, max);
    }
}
