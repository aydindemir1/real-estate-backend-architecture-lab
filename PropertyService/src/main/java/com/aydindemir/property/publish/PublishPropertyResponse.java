package com.aydindemir.property.publish;

import java.time.Instant;

public record PublishPropertyResponse(
        String propertyId,
        String status,
        Instant publishedAt,
        Instant updatedAt,
        Long version) {

    static PublishPropertyResponse from(PublishPropertyResult result) {
        return new PublishPropertyResponse(
                result.propertyId(), result.status(), result.publishedAt(), result.updatedAt(), result.version());
    }
}
