package com.aydindemir.seller.presentation.rest.response;

import java.time.Instant;
import java.util.UUID;

public record SellerResponse(
        UUID sellerId,
        UUID userId,
        String displayName,
        String status,
        Instant createdAt,
        Instant updatedAt) {
}
