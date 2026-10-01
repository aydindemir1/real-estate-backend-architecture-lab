package com.aydindemir.property.shared.domain.model;

import java.util.Objects;
import java.util.UUID;

public record PropertyId(UUID value) {
    public PropertyId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static PropertyId newId() {
        return new PropertyId(UUID.randomUUID());
    }

    public static PropertyId from(String value) {
        return new PropertyId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
