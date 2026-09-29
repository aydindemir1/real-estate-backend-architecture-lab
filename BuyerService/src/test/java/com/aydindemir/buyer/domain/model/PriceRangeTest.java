package com.aydindemir.buyer.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PriceRangeTest {

    private static final Currency TRY = Currency.getInstance("TRY");

    @Test
    void shouldCreateValidPriceRange() {
        PriceRange range = new PriceRange(
                new BigDecimal("1000000"),
                new BigDecimal("5000000"),
                TRY
        );

        assertThat(range.min()).isEqualByComparingTo("1000000");
        assertThat(range.max()).isEqualByComparingTo("5000000");
        assertThat(range.currency()).isEqualTo(TRY);
    }

    @Test
    void shouldAllowEqualMinAndMax() {
        PriceRange range = new PriceRange(
                new BigDecimal("2500000"),
                new BigDecimal("2500000"),
                TRY
        );

        assertThat(range.min()).isEqualByComparingTo(range.max());
    }

    @Test
    void shouldRejectNegativeMin() {
        assertThatThrownBy(() -> new PriceRange(
                new BigDecimal("-1"),
                BigDecimal.TEN,
                TRY
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectMaxLowerThanMin() {
        assertThatThrownBy(() -> new PriceRange(
                new BigDecimal("100"),
                new BigDecimal("99"),
                TRY
        )).isInstanceOf(IllegalArgumentException.class);
    }
}
