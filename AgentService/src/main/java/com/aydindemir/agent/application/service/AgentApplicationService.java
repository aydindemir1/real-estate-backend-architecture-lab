package com.aydindemir.agent.application.service;

import com.aydindemir.agent.application.command.ChangeAvailabilityCommand;
import com.aydindemir.agent.application.command.CreateAgentCommand;
import com.aydindemir.agent.application.exception.AgentAlreadyExistsForUserException;
import com.aydindemir.agent.application.exception.AgentNotFoundException;
import com.aydindemir.agent.application.exception.DuplicateLicenseNumberException;
import com.aydindemir.agent.application.query.GetAgentQuery;
import com.aydindemir.agent.application.result.AgentResult;
import com.aydindemir.agent.application.usecase.ChangeAvailabilityUseCase;
import com.aydindemir.agent.application.usecase.CreateAgentUseCase;
import com.aydindemir.agent.application.usecase.GetAgentUseCase;
import com.aydindemir.agent.domain.model.AgencyInfo;
import com.aydindemir.agent.domain.model.Agent;
import com.aydindemir.agent.domain.model.AgentId;
import com.aydindemir.agent.domain.model.LicenseNumber;
import com.aydindemir.agent.domain.model.UserId;
import com.aydindemir.agent.domain.repository.AgentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Objects;

@Service
public class AgentApplicationService
        implements CreateAgentUseCase, GetAgentUseCase, ChangeAvailabilityUseCase {

    private final AgentRepository agentRepository;
    private final Clock clock;

    public AgentApplicationService(AgentRepository agentRepository, Clock clock) {
        this.agentRepository = Objects.requireNonNull(agentRepository, "Agent repository must not be null");
        this.clock = Objects.requireNonNull(clock, "Clock must not be null");
    }

    @Override
    @Transactional
    public AgentResult createAgent(CreateAgentCommand command) {
        Objects.requireNonNull(command, "Create agent command must not be null");

        UserId userId = new UserId(command.userId());
        LicenseNumber licenseNumber = new LicenseNumber(command.licenseNumber());

        if (agentRepository.existsByUserId(userId)) {
            throw new AgentAlreadyExistsForUserException(userId);
        }

        if (agentRepository.existsByLicenseNumber(licenseNumber)) {
            throw new DuplicateLicenseNumberException(licenseNumber);
        }

        AgencyInfo agencyInfo = new AgencyInfo(
                command.agencyName(),
                command.agencyRegistrationNumber(),
                command.officePhone()
        );

        Agent agent = Agent.create(userId, licenseNumber, agencyInfo, clock);
        Agent savedAgent = agentRepository.save(agent);

        return toResult(savedAgent);
    }

    @Override
    @Transactional(readOnly = true)
    public AgentResult getAgent(GetAgentQuery query) {
        Objects.requireNonNull(query, "Get agent query must not be null");

        AgentId agentId = new AgentId(query.agentId());

        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new AgentNotFoundException(agentId));

        return toResult(agent);
    }

    @Override
    @Transactional
    public AgentResult changeAvailability(ChangeAvailabilityCommand command) {
        Objects.requireNonNull(command, "Change availability command must not be null");

        AgentId agentId = new AgentId(command.agentId());

        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new AgentNotFoundException(agentId));

        agent.changeAvailability(command.availability(), clock);

        Agent savedAgent = agentRepository.save(agent);
        return toResult(savedAgent);
    }

    private AgentResult toResult(Agent agent) {
        return new AgentResult(
                agent.id().value(),
                agent.userId().value(),
                agent.licenseNumber().value(),
                agent.agencyInfo().agencyName(),
                agent.agencyInfo().registrationNumber(),
                agent.agencyInfo().officePhone(),
                agent.status(),
                agent.availability(),
                agent.createdAt(),
                agent.updatedAt()
        );
    }
}
