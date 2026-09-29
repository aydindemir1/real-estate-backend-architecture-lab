package com.aydindemir.buyer.application.port.out;

import com.aydindemir.buyer.domain.model.BuyerPreferences;

public interface SaveBuyerPreferencesPort {

    BuyerPreferences save(BuyerPreferences preferences);
}
