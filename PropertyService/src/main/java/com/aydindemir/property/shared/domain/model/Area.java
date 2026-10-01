package com.aydindemir.property.shared.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public record Area(BigDecimal squareMeters) {
    public Area {
        Objects.requireNonNull(squareMeters, "squareMeters must not be null");
        if (squareMeters.signum() <= 0) {
            throw new IllegalArgumentException("squareMeters must be greater than zero");
        }
    }
}
