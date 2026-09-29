package com.aydindemir.buyer.domain.model;

import java.util.Objects;
import java.util.UUID;

public record BuyerId(UUID value) {

    public BuyerId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static BuyerId of(UUID value) {
        return new BuyerId(value);
    }

    public static BuyerId from(String value) {
        Objects.requireNonNull(value, "value must not be null");
        return new BuyerId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
