package com.aydindemir.seller.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PropertyDraftDataTest {

    @Test
    void normalizesTextAndCurrency() {
        PropertyDraftData draft = new PropertyDraftData(
                "  Daire  ",
                "  Açıklama  ",
                "  apartment  ",
                "  Kocaeli  ",
                "  Gebze  ",
                "  Örnek Mah. No:1  ",
                new BigDecimal("3500000"),
                " try ",
                new BigDecimal("120"),
                3);

        assertEquals("Daire", draft.title());
        assertEquals("TRY", draft.currency());
        assertEquals("Gebze", draft.district());
    }

    @Test
    void rejectsNonPositivePrice() {
        assertThrows(IllegalArgumentException.class, () -> new PropertyDraftData(
                "Daire",
                "Açıklama",
                "APARTMENT",
                "Kocaeli",
                "Gebze",
                "Adres",
                BigDecimal.ZERO,
                "TRY",
                new BigDecimal("120"),
                3));
    }

    @Test
    void rejectsNonPositiveArea() {
        assertThrows(IllegalArgumentException.class, () -> new PropertyDraftData(
                "Daire",
                "Açıklama",
                "APARTMENT",
                "Kocaeli",
                "Gebze",
                "Adres",
                new BigDecimal("3500000"),
                "TRY",
                BigDecimal.ZERO,
                3));
    }

    @Test
    void rejectsNegativeRoomCount() {
        assertThrows(IllegalArgumentException.class, () -> new PropertyDraftData(
                "Daire",
                "Açıklama",
                "APARTMENT",
                "Kocaeli",
                "Gebze",
                "Adres",
                new BigDecimal("3500000"),
                "TRY",
                new BigDecimal("120"),
                -1));
    }
}
