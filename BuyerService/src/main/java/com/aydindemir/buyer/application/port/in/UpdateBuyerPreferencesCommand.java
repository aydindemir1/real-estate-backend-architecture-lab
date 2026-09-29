package com.aydindemir.buyer.application.port.in;

import com.aydindemir.buyer.domain.model.AreaRange;
import com.aydindemir.buyer.domain.model.BuyerId;
import com.aydindemir.buyer.domain.model.LocationPreference;
import com.aydindemir.buyer.domain.model.NotificationSettings;
import com.aydindemir.buyer.domain.model.PriceRange;
import com.aydindemir.buyer.domain.model.RoomRange;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public record UpdateBuyerPreferencesCommand(
        BuyerId buyerId,
        PriceRange priceRange,
        List<LocationPreference> preferredLocations,
        Set<String> propertyTypes,
        RoomRange roomRange,
        AreaRange areaRange,
        Set<String> preferredFeatures,
        NotificationSettings notificationSettings
) {

    public UpdateBuyerPreferencesCommand {
        Objects.requireNonNull(buyerId, "buyerId must not be null");
        Objects.requireNonNull(priceRange, "priceRange must not be null");
        preferredLocations = preferredLocations == null ? List.of() : List.copyOf(preferredLocations);
        propertyTypes = propertyTypes == null ? Set.of() : Set.copyOf(propertyTypes);
        preferredFeatures = preferredFeatures == null ? Set.of() : Set.copyOf(preferredFeatures);
        Objects.requireNonNull(notificationSettings, "notificationSettings must not be null");
    }
}
