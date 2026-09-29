package com.aydindemir.buyer.application.port.out;

import com.aydindemir.buyer.domain.model.BuyerId;
import com.aydindemir.buyer.domain.model.BuyerPreferences;

import java.util.Optional;

public interface LoadBuyerPreferencesPort {

    Optional<BuyerPreferences> load(BuyerId buyerId);
}
