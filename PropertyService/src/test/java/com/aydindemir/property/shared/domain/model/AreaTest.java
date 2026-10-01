package com.aydindemir.property.shared.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AreaTest {

    @Test
    void areaMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> new Area(new BigDecimal("-1")));
    }
}
