package com.aydindemir.property.shared.persistence.adapter;

import com.aydindemir.property.shared.domain.exception.PropertyConcurrentModificationException;
import com.aydindemir.property.shared.persistence.repository.SpringDataPropertyRepository;
import com.aydindemir.property.support.PropertyMongoContainerTestBase;
import com.aydindemir.property.support.PropertyTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PropertyOptimisticLockingIntegrationTest extends PropertyMongoContainerTestBase {

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
    void rejectsStaleConcurrentSave() {
        var persisted = adapter.save(PropertyTestFactory.draft(CLOCK));
        var firstCopy = adapter.findById(persisted.propertyId()).orElseThrow();
        var staleCopy = adapter.findById(persisted.propertyId()).orElseThrow();

        firstCopy.publish(CLOCK);
        adapter.save(firstCopy);

        staleCopy.publish(CLOCK);

        assertThrows(PropertyConcurrentModificationException.class, () -> adapter.save(staleCopy));
    }
}
