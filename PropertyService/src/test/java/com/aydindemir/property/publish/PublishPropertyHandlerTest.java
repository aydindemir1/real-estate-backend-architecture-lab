package com.aydindemir.property.publish;

import com.aydindemir.property.shared.domain.exception.InvalidPropertyStateException;
import com.aydindemir.property.shared.domain.repository.PropertyRepository;
import com.aydindemir.property.support.PropertyTestFactory;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PublishPropertyHandlerTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-10-02T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void publishesDraftAndSavesIt() {
        var property = PropertyTestFactory.draft(CLOCK);
        PropertyRepository repository = mock(PropertyRepository.class);
        when(repository.findById(property.propertyId())).thenReturn(Optional.of(property));
        when(repository.save(any())).thenAnswer(invocation -> {
            var saved = invocation.getArgument(0, com.aydindemir.property.shared.domain.model.Property.class);
            saved.assignVersion(1L);
            return saved;
        });

        var result = new PublishPropertyHandler(repository, CLOCK)
                .handle(new PublishPropertyCommand(property.propertyId()));

        assertEquals("PUBLISHED", result.status());
        assertEquals(1L, result.version());
    }

    @Test
    void rejectsRepublish() {
        var property = PropertyTestFactory.draft(CLOCK);
        property.publish(CLOCK);
        PropertyRepository repository = mock(PropertyRepository.class);
        when(repository.findById(property.propertyId())).thenReturn(Optional.of(property));

        var handler = new PublishPropertyHandler(repository, CLOCK);

        assertThrows(InvalidPropertyStateException.class,
                () -> handler.handle(new PublishPropertyCommand(property.propertyId())));
    }
}
