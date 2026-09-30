package com.aydindemir.seller.presentation.rest.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ListingSubmissionResponse(
        UUID submissionId,
        UUID sellerId,
        String status,
        Instant createdAt,
        Instant updatedAt,
        String title,
        String description,
        String propertyType,
        String city,
        String district,
        String addressLine,
        BigDecimal priceAmount,
        String currency,
        BigDecimal area,
        int roomCount) {
}
