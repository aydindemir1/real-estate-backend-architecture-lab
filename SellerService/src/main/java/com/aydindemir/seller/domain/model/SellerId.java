package com.aydindemir.seller.domain.model;

import java.util.Objects;
import java.util.UUID;

public record SellerId(UUID value) {

    public SellerId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static SellerId of(UUID value) {
        return new SellerId(value);
    }

    public static SellerId from(String value) {
        Objects.requireNonNull(value, "value must not be null");
        return new SellerId(UUID.fromString(value));
    }

    public static SellerId newId() {
        return new SellerId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
