package com.aydindemir.agent.application.command;

import com.aydindemir.agent.domain.model.AvailabilityStatus;

import java.util.UUID;

public record ChangeAvailabilityCommand(
        UUID agentId,
        AvailabilityStatus availability
) {
}
