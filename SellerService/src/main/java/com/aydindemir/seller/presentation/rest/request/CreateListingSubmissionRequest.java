package com.aydindemir.seller.presentation.rest.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateListingSubmissionRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String propertyType,
        @NotBlank String city,
        @NotBlank String district,
        @NotBlank String addressLine,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal priceAmount,
        @NotBlank String currency,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal area,
        @Min(0) int roomCount) {
}
