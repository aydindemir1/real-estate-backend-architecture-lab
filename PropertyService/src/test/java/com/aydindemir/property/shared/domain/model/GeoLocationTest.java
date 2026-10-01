package com.aydindemir.property.shared.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

class GeoLocationTest {

    @Test
    void latitudeMustStayWithinEarthBounds() {
        assertThrows(IllegalArgumentException.class,
                () -> new GeoLocation(new BigDecimal("90.1"), BigDecimal.ZERO));
    }

    @Test
    void longitudeMustStayWithinEarthBounds() {
        assertThrows(IllegalArgumentException.class,
                () -> new GeoLocation(BigDecimal.ZERO, new BigDecimal("-180.1")));
    }
}
