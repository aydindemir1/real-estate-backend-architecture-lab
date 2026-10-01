package com.aydindemir.property.getbyid;

import com.aydindemir.property.shared.domain.exception.PropertyNotFoundException;
import com.aydindemir.property.shared.domain.repository.PropertyRepository;
import com.aydindemir.property.support.PropertyTestFactory;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetPropertyHandlerTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-10-02T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void returnsPropertyWhenFound() {
        var property = PropertyTestFactory.draft(CLOCK);
        PropertyRepository repository = mock(PropertyRepository.class);
        when(repository.findById(property.propertyId())).thenReturn(Optional.of(property));

        var result = new GetPropertyHandler(repository).handle(new GetPropertyQuery(property.propertyId()));

        assertEquals(property.propertyId().toString(), result.propertyId());
        assertEquals("DRAFT", result.status());
    }

    @Test
    void throwsWhenPropertyDoesNotExist() {
        var property = PropertyTestFactory.draft(CLOCK);
        PropertyRepository repository = mock(PropertyRepository.class);
        when(repository.findById(property.propertyId())).thenReturn(Optional.empty());

        var handler = new GetPropertyHandler(repository);

        assertThrows(PropertyNotFoundException.class,
                () -> handler.handle(new GetPropertyQuery(property.propertyId())));
    }
}
