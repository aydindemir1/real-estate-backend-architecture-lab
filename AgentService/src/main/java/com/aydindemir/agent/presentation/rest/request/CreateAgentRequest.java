package com.aydindemir.agent.presentation.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateAgentRequest(
        @NotNull UUID userId,
        @NotBlank @Size(max = 128) String licenseNumber,
        @NotBlank @Size(max = 255) String agencyName,
        @Size(max = 128) String agencyRegistrationNumber,
        @Size(max = 64) String officePhone
) {
}
