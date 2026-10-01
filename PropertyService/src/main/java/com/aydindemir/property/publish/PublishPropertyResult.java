package com.aydindemir.property.publish;

import java.time.Instant;

public record PublishPropertyResult(
        String propertyId,
        String status,
        Instant publishedAt,
        Instant updatedAt,
        Long version) {
}
