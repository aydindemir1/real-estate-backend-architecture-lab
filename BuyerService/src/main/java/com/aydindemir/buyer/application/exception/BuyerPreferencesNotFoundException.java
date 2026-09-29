package com.aydindemir.buyer.application.exception;

import com.aydindemir.buyer.domain.model.BuyerId;

public class BuyerPreferencesNotFoundException extends RuntimeException {

    public BuyerPreferencesNotFoundException(BuyerId buyerId) {
        super("Buyer preferences not found: " + buyerId.value());
    }
}
