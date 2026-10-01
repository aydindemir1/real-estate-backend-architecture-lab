package com.aydindemir.property.shared.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyTest {

    @Test
    void priceMustBePositive() {
        assertThrows(IllegalArgumentException.class,
                () -> new Money(BigDecimal.ZERO, Currency.getInstance("TRY")));
    }
}
