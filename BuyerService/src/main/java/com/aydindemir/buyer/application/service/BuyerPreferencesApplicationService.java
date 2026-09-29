package com.aydindemir.buyer.application.service;

import com.aydindemir.buyer.application.exception.BuyerPreferencesNotFoundException;
import com.aydindemir.buyer.application.port.in.AddSavedSearchCommand;
import com.aydindemir.buyer.application.port.in.AddSavedSearchUseCase;
import com.aydindemir.buyer.application.port.in.GetBuyerPreferencesQuery;
import com.aydindemir.buyer.application.port.in.GetBuyerPreferencesUseCase;
import com.aydindemir.buyer.application.port.in.UpdateBuyerPreferencesCommand;
import com.aydindemir.buyer.application.port.in.UpdateBuyerPreferencesUseCase;
import com.aydindemir.buyer.application.port.out.LoadBuyerPreferencesPort;
import com.aydindemir.buyer.application.port.out.SaveBuyerPreferencesPort;
import com.aydindemir.buyer.domain.model.BuyerPreferences;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Objects;

@Service
public class BuyerPreferencesApplicationService
        implements UpdateBuyerPreferencesUseCase, GetBuyerPreferencesUseCase, AddSavedSearchUseCase {

    private final LoadBuyerPreferencesPort loadBuyerPreferencesPort;
    private final SaveBuyerPreferencesPort saveBuyerPreferencesPort;
    private final Clock clock;

    public BuyerPreferencesApplicationService(
            LoadBuyerPreferencesPort loadBuyerPreferencesPort,
            SaveBuyerPreferencesPort saveBuyerPreferencesPort,
            Clock clock
    ) {
        this.loadBuyerPreferencesPort = Objects.requireNonNull(
                loadBuyerPreferencesPort,
                "Load buyer preferences port must not be null"
        );
        this.saveBuyerPreferencesPort = Objects.requireNonNull(
                saveBuyerPreferencesPort,
                "Save buyer preferences port must not be null"
        );
        this.clock = Objects.requireNonNull(clock, "Clock must not be null");
    }

    @Override
    public BuyerPreferencesResult update(UpdateBuyerPreferencesCommand command) {
        Objects.requireNonNull(command, "Update buyer preferences command must not be null");

        BuyerPreferences preferences = loadBuyerPreferencesPort.load(command.buyerId())
                .map(existing -> updateExisting(existing, command))
                .orElseGet(() -> createNew(command));

        return toResult(saveBuyerPreferencesPort.save(preferences));
    }

    @Override
    public BuyerPreferencesResult get(GetBuyerPreferencesQuery query) {
        Objects.requireNonNull(query, "Get buyer preferences query must not be null");

        BuyerPreferences preferences = loadBuyerPreferencesPort.load(query.buyerId())
                .orElseThrow(() -> new BuyerPreferencesNotFoundException(query.buyerId()));

        return toResult(preferences);
    }

    @Override
    public BuyerPreferencesResult add(AddSavedSearchCommand command) {
        Objects.requireNonNull(command, "Add saved search command must not be null");

        BuyerPreferences preferences = loadBuyerPreferencesPort.load(command.buyerId())
                .orElseThrow(() -> new BuyerPreferencesNotFoundException(command.buyerId()));

        preferences.addSavedSearch(command.savedSearch(), clock);

        return toResult(saveBuyerPreferencesPort.save(preferences));
    }

    private BuyerPreferences updateExisting(
            BuyerPreferences preferences,
            UpdateBuyerPreferencesCommand command
    ) {
        preferences.updatePreferences(
                command.priceRange(),
                command.preferredLocations(),
                command.propertyTypes(),
                command.roomRange(),
                command.areaRange(),
                command.preferredFeatures(),
                command.notificationSettings(),
                clock
        );
        return preferences;
    }

    private BuyerPreferences createNew(UpdateBuyerPreferencesCommand command) {
        return BuyerPreferences.create(
                command.buyerId(),
                command.priceRange(),
                command.preferredLocations(),
                command.propertyTypes(),
                command.roomRange(),
                command.areaRange(),
                command.preferredFeatures(),
                command.notificationSettings(),
                clock
        );
    }

    private BuyerPreferencesResult toResult(BuyerPreferences preferences) {
        return new BuyerPreferencesResult(
                preferences.buyerId(),
                preferences.priceRange(),
                preferences.preferredLocations(),
                preferences.propertyTypes(),
                preferences.roomRange(),
                preferences.areaRange(),
                preferences.preferredFeatures(),
                preferences.notificationSettings(),
                preferences.savedSearches(),
                preferences.createdAt(),
                preferences.updatedAt()
        );
    }
}
