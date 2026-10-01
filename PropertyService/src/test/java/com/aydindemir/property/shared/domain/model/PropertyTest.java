package com.aydindemir.property.shared.domain.model;

import com.aydindemir.property.shared.domain.exception.InvalidPropertyStateException;
import com.aydindemir.property.support.PropertyTestFactory;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PropertyTest {

    private static final Instant NOW = Instant.parse("2026-10-02T00:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    @Test
    void draftCanBePublished() {
        Property property = PropertyTestFactory.draft(CLOCK);

        property.publish(CLOCK);

        assertEquals(PropertyStatus.PUBLISHED, property.status());
        assertEquals(NOW, property.publishedAt());
        assertEquals(NOW, property.updatedAt());
    }

    @Test
    void publishedPropertyCannotBePublishedAgain() {
        Property property = PropertyTestFactory.draft(CLOCK);
        property.publish(CLOCK);

        assertThrows(InvalidPropertyStateException.class, () -> property.publish(CLOCK));
    }
}
