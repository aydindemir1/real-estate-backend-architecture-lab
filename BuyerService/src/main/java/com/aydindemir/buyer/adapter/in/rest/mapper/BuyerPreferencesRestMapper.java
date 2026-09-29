package com.aydindemir.buyer.adapter.in.rest.mapper;

import com.aydindemir.buyer.adapter.in.rest.request.AddSavedSearchRequest;
import com.aydindemir.buyer.adapter.in.rest.request.UpdateBuyerPreferencesRequest;
import com.aydindemir.buyer.adapter.in.rest.response.BuyerPreferencesResponse;
import com.aydindemir.buyer.application.port.in.AddSavedSearchCommand;
import com.aydindemir.buyer.application.port.in.GetBuyerPreferencesQuery;
import com.aydindemir.buyer.application.port.in.UpdateBuyerPreferencesCommand;
import com.aydindemir.buyer.application.service.BuyerPreferencesResult;
import com.aydindemir.buyer.domain.model.AreaRange;
import com.aydindemir.buyer.domain.model.BuyerId;
import com.aydindemir.buyer.domain.model.LocationPreference;
import com.aydindemir.buyer.domain.model.NotificationSettings;
import com.aydindemir.buyer.domain.model.PriceRange;
import com.aydindemir.buyer.domain.model.RoomRange;
import com.aydindemir.buyer.domain.model.SavedSearch;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class BuyerPreferencesRestMapper {

    private final Clock clock;

    public BuyerPreferencesRestMapper(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "Clock must not be null");
    }

    public UpdateBuyerPreferencesCommand toUpdateCommand(
            UUID buyerId,
            UpdateBuyerPreferencesRequest request
    ) {
        Objects.requireNonNull(buyerId, "buyerId must not be null");
        Objects.requireNonNull(request, "request must not be null");

        return new UpdateBuyerPreferencesCommand(
                BuyerId.of(buyerId),
                new PriceRange(
                        request.minPrice(),
                        request.maxPrice(),
                        Currency.getInstance(request.currency().trim().toUpperCase())
                ),
                mapUpdateLocations(request.preferredLocations()),
                request.propertyTypes(),
                toRoomRange(request.minRooms(), request.maxRooms()),
                toAreaRange(request.minArea(), request.maxArea()),
                request.preferredFeatures(),
                new NotificationSettings(
                        request.notificationSettings().emailEnabled(),
                        request.notificationSettings().pushEnabled(),
                        request.notificationSettings().smsEnabled()
                )
        );
    }

    public GetBuyerPreferencesQuery toGetQuery(UUID buyerId) {
        return new GetBuyerPreferencesQuery(BuyerId.of(
                Objects.requireNonNull(buyerId, "buyerId must not be null")
        ));
    }

    public AddSavedSearchCommand toAddSavedSearchCommand(
            UUID buyerId,
            AddSavedSearchRequest request
    ) {
        Objects.requireNonNull(buyerId, "buyerId must not be null");
        Objects.requireNonNull(request, "request must not be null");

        PriceRange priceRange = null;
        if (request.minPrice() != null || request.maxPrice() != null || request.currency() != null) {
            if (request.minPrice() == null || request.maxPrice() == null
                    || request.currency() == null || request.currency().isBlank()) {
                throw new IllegalArgumentException("Saved search price range requires minPrice, maxPrice and currency together");
            }
            priceRange = new PriceRange(
                    request.minPrice(),
                    request.maxPrice(),
                    Currency.getInstance(request.currency().trim().toUpperCase())
            );
        }

        SavedSearch savedSearch = new SavedSearch(
                UUID.randomUUID(),
                request.name(),
                priceRange,
                mapSavedSearchLocations(request.locations()),
                request.propertyTypes(),
                toRoomRange(request.minRooms(), request.maxRooms()),
                toAreaRange(request.minArea(), request.maxArea()),
                request.preferredFeatures(),
                Instant.now(clock)
        );

        return new AddSavedSearchCommand(BuyerId.of(buyerId), savedSearch);
    }

    public BuyerPreferencesResponse toResponse(BuyerPreferencesResult result) {
        Objects.requireNonNull(result, "result must not be null");

        return new BuyerPreferencesResponse(
                result.buyerId().value(),
                toMoneyRangeResponse(result.priceRange()),
                result.preferredLocations().stream().map(this::toLocationResponse).toList(),
                result.propertyTypes(),
                toIntegerRangeResponse(result.roomRange()),
                toDecimalRangeResponse(result.areaRange()),
                result.preferredFeatures(),
                new BuyerPreferencesResponse.NotificationSettingsResponse(
                        result.notificationSettings().emailEnabled(),
                        result.notificationSettings().pushEnabled(),
                        result.notificationSettings().smsEnabled()
                ),
                result.savedSearches().stream().map(this::toSavedSearchResponse).toList(),
                result.createdAt(),
                result.updatedAt()
        );
    }

    private List<LocationPreference> mapUpdateLocations(
            List<UpdateBuyerPreferencesRequest.LocationRequest> locations
    ) {
        if (locations == null) {
            return List.of();
        }
        return locations.stream()
                .map(location -> new LocationPreference(location.city(), location.district()))
                .toList();
    }

    private List<LocationPreference> mapSavedSearchLocations(
            List<AddSavedSearchRequest.LocationRequest> locations
    ) {
        if (locations == null) {
            return List.of();
        }
        return locations.stream()
                .map(location -> new LocationPreference(location.city(), location.district()))
                .toList();
    }

    private RoomRange toRoomRange(Integer min, Integer max) {
        if (min == null && max == null) {
            return null;
        }
        if (min == null || max == null) {
            throw new IllegalArgumentException("Room range requires minRooms and maxRooms together");
        }
        return new RoomRange(min, max);
    }

    private AreaRange toAreaRange(java.math.BigDecimal min, java.math.BigDecimal max) {
        if (min == null && max == null) {
            return null;
        }
        if (min == null || max == null) {
            throw new IllegalArgumentException("Area range requires minArea and maxArea together");
        }
        return new AreaRange(min, max);
    }

    private BuyerPreferencesResponse.MoneyRangeResponse toMoneyRangeResponse(PriceRange range) {
        return new BuyerPreferencesResponse.MoneyRangeResponse(
                range.min(),
                range.max(),
                range.currency().getCurrencyCode()
        );
    }

    private BuyerPreferencesResponse.LocationResponse toLocationResponse(LocationPreference location) {
        return new BuyerPreferencesResponse.LocationResponse(location.city(), location.district());
    }

    private BuyerPreferencesResponse.RangeResponse<Integer> toIntegerRangeResponse(RoomRange range) {
        return range == null ? null : new BuyerPreferencesResponse.RangeResponse<>(range.min(), range.max());
    }

    private BuyerPreferencesResponse.RangeResponse<java.math.BigDecimal> toDecimalRangeResponse(AreaRange range) {
        return range == null ? null : new BuyerPreferencesResponse.RangeResponse<>(range.min(), range.max());
    }

    private BuyerPreferencesResponse.SavedSearchResponse toSavedSearchResponse(SavedSearch search) {
        return new BuyerPreferencesResponse.SavedSearchResponse(
                search.id(),
                search.name(),
                search.priceRange() == null ? null : toMoneyRangeResponse(search.priceRange()),
                search.locations().stream().map(this::toLocationResponse).toList(),
                search.propertyTypes(),
                toIntegerRangeResponse(search.roomRange()),
                toDecimalRangeResponse(search.areaRange()),
                search.preferredFeatures(),
                search.createdAt()
        );
    }
}
