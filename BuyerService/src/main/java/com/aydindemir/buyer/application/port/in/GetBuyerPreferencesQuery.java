package com.aydindemir.buyer.application.port.in;

import com.aydindemir.buyer.domain.model.BuyerId;

import java.util.Objects;

public record GetBuyerPreferencesQuery(BuyerId buyerId) {

    public GetBuyerPreferencesQuery {
        Objects.requireNonNull(buyerId, "buyerId must not be null");
    }
}
