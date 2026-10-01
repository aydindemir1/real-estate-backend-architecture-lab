package com.aydindemir.property.shared.domain.model;

import java.util.Objects;
import java.util.UUID;

public record SellerId(UUID value) {
    public SellerId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static SellerId from(String value) {
        return new SellerId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
