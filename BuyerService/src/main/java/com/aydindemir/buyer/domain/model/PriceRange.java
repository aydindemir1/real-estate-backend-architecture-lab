package com.aydindemir.buyer.domain.model;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

public record PriceRange(BigDecimal min, BigDecimal max, Currency currency) {

    public PriceRange {
        Objects.requireNonNull(min, "min must not be null");
        Objects.requireNonNull(max, "max must not be null");
        Objects.requireNonNull(currency, "currency must not be null");

        if (min.signum() < 0) {
            throw new IllegalArgumentException("min must be greater than or equal to zero");
        }
        if (max.compareTo(min) < 0) {
            throw new IllegalArgumentException("max must be greater than or equal to min");
        }
    }
}
