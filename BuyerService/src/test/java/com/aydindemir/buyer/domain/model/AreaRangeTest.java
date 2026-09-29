package com.aydindemir.buyer.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AreaRangeTest {

    @Test
    void shouldCreateValidAreaRange() {
        AreaRange range = new AreaRange(
                new BigDecimal("80"),
                new BigDecimal("180")
        );

        assertThat(range.min()).isEqualByComparingTo("80");
        assertThat(range.max()).isEqualByComparingTo("180");
    }

    @Test
    void shouldAllowZeroAndEqualValues() {
        AreaRange range = new AreaRange(BigDecimal.ZERO, BigDecimal.ZERO);

        assertThat(range.min()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(range.max()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void shouldRejectNegativeMin() {
        assertThatThrownBy(() -> new AreaRange(
                new BigDecimal("-0.01"),
                BigDecimal.ONE
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectMaxLowerThanMin() {
        assertThatThrownBy(() -> new AreaRange(
                new BigDecimal("120"),
                new BigDecimal("119.99")
        )).isInstanceOf(IllegalArgumentException.class);
    }
}
