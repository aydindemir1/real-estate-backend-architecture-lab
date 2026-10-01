package com.aydindemir.property.publish;

import com.aydindemir.property.shared.domain.exception.PropertyNotFoundException;
import com.aydindemir.property.shared.domain.model.Property;
import com.aydindemir.property.shared.domain.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class PublishPropertyHandler {

    private final PropertyRepository repository;
    private final Clock clock;

    public PublishPropertyHandler(PropertyRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public PublishPropertyResult handle(PublishPropertyCommand command) {
        Property property = repository.findById(command.propertyId())
                .orElseThrow(() -> new PropertyNotFoundException(command.propertyId().toString()));

        property.publish(clock);
        Property saved = repository.save(property);

        return new PublishPropertyResult(
                saved.propertyId().toString(),
                saved.status().name(),
                saved.publishedAt(),
                saved.updatedAt(),
                saved.version());
    }
}
