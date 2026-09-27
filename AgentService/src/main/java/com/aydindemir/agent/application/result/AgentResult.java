package com.aydindemir.agent.application.result;

import com.aydindemir.agent.domain.model.AgentStatus;
import com.aydindemir.agent.domain.model.AvailabilityStatus;

import java.time.Instant;
import java.util.UUID;

public record AgentResult(
        UUID agentId,
        UUID userId,
        String licenseNumber,
        String agencyName,
        String agencyRegistrationNumber,
        String officePhone,
        AgentStatus status,
        AvailabilityStatus availability,
        Instant createdAt,
        Instant updatedAt
) {
}
