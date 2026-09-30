package com.aydindemir.seller.application.service;

import com.aydindemir.seller.domain.model.Seller;
import com.aydindemir.seller.domain.model.SellerId;
import com.aydindemir.seller.domain.model.SellerStatus;
import com.aydindemir.seller.domain.model.UserId;

import java.time.Instant;
import java.util.Objects;

public record SellerResult(
        SellerId sellerId,
        UserId userId,
        String displayName,
        SellerStatus status,
        Instant createdAt,
        Instant updatedAt) {

    public SellerResult {
        Objects.requireNonNull(sellerId, "sellerId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(displayName, "displayName must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    public static SellerResult from(Seller seller) {
        Objects.requireNonNull(seller, "seller must not be null");
        return new SellerResult(
                seller.sellerId(),
                seller.userId(),
                seller.displayName(),
                seller.status(),
                seller.createdAt(),
                seller.updatedAt());
    }
}
