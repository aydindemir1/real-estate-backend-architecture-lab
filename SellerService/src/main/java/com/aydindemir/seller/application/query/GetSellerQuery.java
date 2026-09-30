package com.aydindemir.seller.application.query;

import com.aydindemir.seller.domain.model.SellerId;

import java.util.Objects;

public record GetSellerQuery(SellerId sellerId) {

    public GetSellerQuery {
        Objects.requireNonNull(sellerId, "sellerId must not be null");
    }
}
