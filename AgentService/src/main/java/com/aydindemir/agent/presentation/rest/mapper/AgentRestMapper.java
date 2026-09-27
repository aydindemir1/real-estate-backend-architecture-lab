package com.aydindemir.agent.presentation.rest.mapper;

import com.aydindemir.agent.application.command.ChangeAvailabilityCommand;
import com.aydindemir.agent.application.command.CreateAgentCommand;
import com.aydindemir.agent.application.result.AgentResult;
import com.aydindemir.agent.presentation.rest.request.ChangeAvailabilityRequest;
import com.aydindemir.agent.presentation.rest.request.CreateAgentRequest;
import com.aydindemir.agent.presentation.rest.response.AgentResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AgentRestMapper {

    public CreateAgentCommand toCommand(CreateAgentRequest request) {
        return new CreateAgentCommand(
                request.userId(),
                request.licenseNumber(),
                request.agencyName(),
                request.agencyRegistrationNumber(),
                request.officePhone()
        );
    }

    public ChangeAvailabilityCommand toCommand(
            UUID agentId,
            ChangeAvailabilityRequest request
    ) {
        return new ChangeAvailabilityCommand(
                agentId,
                request.availability()
        );
    }

    public AgentResponse toResponse(AgentResult result) {
        return new AgentResponse(
                result.agentId(),
                result.userId(),
                result.licenseNumber(),
                result.agencyName(),
                result.agencyRegistrationNumber(),
                result.officePhone(),
                result.status(),
                result.availability(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
