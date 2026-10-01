package com.aydindemir.property.shared.persistence.adapter;

import com.aydindemir.property.shared.persistence.repository.SpringDataPropertyRepository;
import com.aydindemir.property.support.PropertyMongoContainerTestBase;
import com.aydindemir.property.support.PropertyTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MongoPropertyRepositoryAdapterIntegrationTest extends PropertyMongoContainerTestBase {

    private static final Clock CLOCK =
            Clock.fixed(Instant.parse("2026-10-02T00:00:00Z"), ZoneOffset.UTC);

    @Autowired
    private MongoPropertyRepositoryAdapter adapter;

    @Autowired
    private SpringDataPropertyRepository springRepository;

    @BeforeEach
    void cleanCollection() {
        springRepository.deleteAll();
    }

    @Test
    void savesAndLoadsCanonicalPropertyWithoutLosingValueObjects() {
        var draft = PropertyTestFactory.draft(CLOCK);

        var saved = adapter.save(draft);
        var loaded = adapter.findById(saved.propertyId()).orElseThrow();

        assertNotNull(saved.version());
        assertEquals(saved.propertyId(), loaded.propertyId());
        assertEquals(draft.address(), loaded.address());
        assertEquals(draft.geoLocation(), loaded.geoLocation());
        assertEquals(draft.price(), loaded.price());
        assertEquals(draft.area(), loaded.area());
        assertEquals(draft.status(), loaded.status());
        assertEquals(draft.createdAt(), loaded.createdAt());
    }

    @Test
    void missingIdReturnsEmpty() {
        var draft = PropertyTestFactory.draft(CLOCK);

        assertTrue(adapter.findById(draft.propertyId()).isEmpty());
    }
}
