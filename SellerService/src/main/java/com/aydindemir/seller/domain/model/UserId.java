package com.aydindemir.seller.domain.model;

import java.util.Objects;
import java.util.UUID;

public record UserId(UUID value) {

    public UserId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static UserId of(UUID value) {
        return new UserId(value);
    }

    public static UserId from(String value) {
        Objects.requireNonNull(value, "value must not be null");
        return new UserId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
