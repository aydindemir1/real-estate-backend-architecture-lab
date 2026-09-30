package com.aydindemir.seller.presentation.rest.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.Instant;
import java.util.UUID;

public record SubmitListingRequest(
        @NotNull
        @Pattern(regexp = "\\d{4}-\\d{2}")
        String yearMonth,
        @NotNull Instant createdAt,
        @NotNull UUID submissionId) {
}
