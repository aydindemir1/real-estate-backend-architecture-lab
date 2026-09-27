package com.aydindemir.agent.application.command;

import java.util.UUID;

public record CreateAgentCommand(
        UUID userId,
        String licenseNumber,
        String agencyName,
        String agencyRegistrationNumber,
        String officePhone
) {
}
