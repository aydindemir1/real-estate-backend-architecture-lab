package com.aydindemir.buyer.application.port.in;

import com.aydindemir.buyer.application.service.BuyerPreferencesResult;

public interface GetBuyerPreferencesUseCase {

    BuyerPreferencesResult get(GetBuyerPreferencesQuery query);
}
