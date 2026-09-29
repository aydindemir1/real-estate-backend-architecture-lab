package com.aydindemir.buyer.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public record AreaRange(BigDecimal min, BigDecimal max) {

    public AreaRange {
        Objects.requireNonNull(min, "min must not be null");
        Objects.requireNonNull(max, "max must not be null");

        if (min.signum() < 0) {
            throw new IllegalArgumentException("min must be greater than or equal to zero");
        }
        if (max.compareTo(min) < 0) {
            throw new IllegalArgumentException("max must be greater than or equal to min");
        }
    }
}
