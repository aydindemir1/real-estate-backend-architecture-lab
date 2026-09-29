package com.aydindemir.buyer.application.support;

import com.aydindemir.buyer.application.port.out.LoadBuyerPreferencesPort;
import com.aydindemir.buyer.application.port.out.SaveBuyerPreferencesPort;
import com.aydindemir.buyer.domain.model.BuyerId;
import com.aydindemir.buyer.domain.model.BuyerPreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryBuyerPreferencesStore
        implements SaveBuyerPreferencesPort, LoadBuyerPreferencesPort {

    private final Map<BuyerId, BuyerPreferences> storage = new HashMap<>();

    @Override
    public BuyerPreferences save(BuyerPreferences preferences) {
        storage.put(preferences.buyerId(), preferences);
        return preferences;
    }

    @Override
    public Optional<BuyerPreferences> load(BuyerId buyerId) {
        return Optional.ofNullable(storage.get(buyerId));
    }

    public int size() {
        return storage.size();
    }
}
