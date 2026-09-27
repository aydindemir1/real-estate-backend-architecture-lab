package com.aydindemir.agent.presentation.rest.request;

import com.aydindemir.agent.domain.model.AvailabilityStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeAvailabilityRequest(
        @NotNull AvailabilityStatus availability
) {
}
