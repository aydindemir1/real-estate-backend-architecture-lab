package com.aydindemir.seller.domain.model;

import com.aydindemir.seller.domain.exception.SellerNotActiveException;

import java.time.Instant;
import java.util.Objects;

public final class Seller {

    private final SellerId sellerId;
    private final UserId userId;
    private final String displayName;
    private SellerStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private Seller(
            SellerId sellerId,
            UserId userId,
            String displayName,
            SellerStatus status,
            Instant createdAt,
            Instant updatedAt) {
        this.sellerId = Objects.requireNonNull(sellerId, "sellerId must not be null");
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.displayName = requireDisplayName(displayName);
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    public static Seller create(UserId userId, String displayName) {
        Instant now = Instant.now();
        return new Seller(
                SellerId.newId(),
                userId,
                displayName,
                SellerStatus.ACTIVE,
                now,
                now);
    }

    public void changeStatus(SellerStatus newStatus) {
        SellerStatus targetStatus = Objects.requireNonNull(newStatus, "newStatus must not be null");

        if (status == targetStatus) {
            return;
        }

        status = targetStatus;
        updatedAt = Instant.now();
    }

    public void assertCanSubmitListing() {
        if (status != SellerStatus.ACTIVE) {
            throw new SellerNotActiveException(sellerId, status);
        }
    }

    public SellerId sellerId() {
        return sellerId;
    }

    public UserId userId() {
        return userId;
    }

    public String displayName() {
        return displayName;
    }

    public SellerStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    private static String requireDisplayName(String displayName) {
        Objects.requireNonNull(displayName, "displayName must not be null");

        String normalized = displayName.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("displayName must not be blank");
        }

        return normalized;
    }
}
