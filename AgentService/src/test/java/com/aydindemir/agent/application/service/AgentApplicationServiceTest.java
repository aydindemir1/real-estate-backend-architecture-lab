package com.aydindemir.agent.application.service;

import com.aydindemir.agent.application.command.ChangeAvailabilityCommand;
import com.aydindemir.agent.application.command.CreateAgentCommand;
import com.aydindemir.agent.application.exception.AgentAlreadyExistsForUserException;
import com.aydindemir.agent.application.exception.AgentNotFoundException;
import com.aydindemir.agent.application.exception.DuplicateLicenseNumberException;
import com.aydindemir.agent.application.query.GetAgentQuery;
import com.aydindemir.agent.application.result.AgentResult;
import com.aydindemir.agent.domain.exception.InvalidAgentStateException;
import com.aydindemir.agent.domain.model.AgencyInfo;
import com.aydindemir.agent.domain.model.Agent;
import com.aydindemir.agent.domain.model.AgentId;
import com.aydindemir.agent.domain.model.AgentStatus;
import com.aydindemir.agent.domain.model.AvailabilityStatus;
import com.aydindemir.agent.domain.model.LicenseNumber;
import com.aydindemir.agent.domain.model.UserId;
import com.aydindemir.agent.domain.repository.AgentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgentApplicationServiceTest {

    private static final Instant NOW = Instant.parse("2026-09-27T18:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    @Mock
    private AgentRepository agentRepository;

    private AgentApplicationService service;

    @BeforeEach
    void setUp() {
        service = new AgentApplicationService(agentRepository, CLOCK);
    }

    @Test
    void createAgentShouldPersistActiveOfflineAgent() {
        UUID userId = UUID.randomUUID();
        CreateAgentCommand command = new CreateAgentCommand(
                userId,
                "LIC-100",
                "North Realty",
                "REG-100",
                "+90 555 111 22 33"
        );

        when(agentRepository.existsByUserId(new UserId(userId))).thenReturn(false);
        when(agentRepository.existsByLicenseNumber(new LicenseNumber("LIC-100"))).thenReturn(false);
        when(agentRepository.save(any(Agent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AgentResult result = service.createAgent(command);

        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.licenseNumber()).isEqualTo("LIC-100");
        assertThat(result.status()).isEqualTo(AgentStatus.ACTIVE);
        assertThat(result.availability()).isEqualTo(AvailabilityStatus.OFFLINE);
        assertThat(result.createdAt()).isEqualTo(NOW);
        assertThat(result.updatedAt()).isEqualTo(NOW);

        ArgumentCaptor<Agent> captor = ArgumentCaptor.forClass(Agent.class);
        verify(agentRepository).save(captor.capture());
        assertThat(captor.getValue().status()).isEqualTo(AgentStatus.ACTIVE);
        assertThat(captor.getValue().availability()).isEqualTo(AvailabilityStatus.OFFLINE);
    }

    @Test
    void createAgentShouldRejectDuplicateUserBeforeLicenseCheck() {
        UUID userId = UUID.randomUUID();
        CreateAgentCommand command = createCommand(userId, "LIC-100");

        when(agentRepository.existsByUserId(new UserId(userId))).thenReturn(true);

        assertThatThrownBy(() -> service.createAgent(command))
                .isInstanceOf(AgentAlreadyExistsForUserException.class);

        verify(agentRepository, never()).existsByLicenseNumber(any());
        verify(agentRepository, never()).save(any());
    }

    @Test
    void createAgentShouldRejectDuplicateLicense() {
        UUID userId = UUID.randomUUID();
        CreateAgentCommand command = createCommand(userId, "LIC-100");

        when(agentRepository.existsByUserId(new UserId(userId))).thenReturn(false);
        when(agentRepository.existsByLicenseNumber(new LicenseNumber("LIC-100"))).thenReturn(true);

        assertThatThrownBy(() -> service.createAgent(command))
                .isInstanceOf(DuplicateLicenseNumberException.class);

        verify(agentRepository, never()).save(any());
    }

    @Test
    void getAgentShouldReturnResult() {
        Agent agent = persistedAgent(AgentStatus.ACTIVE, AvailabilityStatus.AVAILABLE);
        when(agentRepository.findById(agent.id())).thenReturn(Optional.of(agent));

        AgentResult result = service.getAgent(new GetAgentQuery(agent.id().value()));

        assertThat(result.agentId()).isEqualTo(agent.id().value());
        assertThat(result.status()).isEqualTo(AgentStatus.ACTIVE);
        assertThat(result.availability()).isEqualTo(AvailabilityStatus.AVAILABLE);
    }

    @Test
    void getAgentShouldThrowWhenMissing() {
        AgentId id = new AgentId(UUID.randomUUID());
        when(agentRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getAgent(new GetAgentQuery(id.value())))
                .isInstanceOf(AgentNotFoundException.class);
    }

    @Test
    void changeAvailabilityShouldDelegateBehaviorAndPersist() {
        Agent agent = persistedAgent(AgentStatus.ACTIVE, AvailabilityStatus.OFFLINE);
        when(agentRepository.findById(agent.id())).thenReturn(Optional.of(agent));
        when(agentRepository.save(agent)).thenAnswer(invocation -> invocation.getArgument(0));

        AgentResult result = service.changeAvailability(
                new ChangeAvailabilityCommand(agent.id().value(), AvailabilityStatus.BUSY)
        );

        assertThat(result.availability()).isEqualTo(AvailabilityStatus.BUSY);
        assertThat(agent.updatedAt()).isEqualTo(NOW);
        verify(agentRepository).save(agent);
    }

    @Test
    void changeAvailabilityShouldThrowWhenAgentDoesNotExist() {
        AgentId id = new AgentId(UUID.randomUUID());
        when(agentRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changeAvailability(
                new ChangeAvailabilityCommand(id.value(), AvailabilityStatus.AVAILABLE)
        )).isInstanceOf(AgentNotFoundException.class);

        verify(agentRepository, never()).save(any());
    }

    @Test
    void changeAvailabilityShouldPropagateDomainStateViolation() {
        Agent agent = persistedAgent(AgentStatus.SUSPENDED, AvailabilityStatus.OFFLINE);
        when(agentRepository.findById(agent.id())).thenReturn(Optional.of(agent));

        assertThatThrownBy(() -> service.changeAvailability(
                new ChangeAvailabilityCommand(agent.id().value(), AvailabilityStatus.AVAILABLE)
        )).isInstanceOf(InvalidAgentStateException.class);

        verify(agentRepository, never()).save(any());
    }

    private CreateAgentCommand createCommand(UUID userId, String licenseNumber) {
        return new CreateAgentCommand(
                userId,
                licenseNumber,
                "North Realty",
                null,
                null
        );
    }

    private Agent persistedAgent(
            AgentStatus status,
            AvailabilityStatus availability
    ) {
        return Agent.reconstitute(
                new AgentId(UUID.randomUUID()),
                new UserId(UUID.randomUUID()),
                new LicenseNumber("LIC-200"),
                new AgencyInfo("North Realty", null, null),
                status,
                availability,
                NOW.minusSeconds(60),
                NOW.minusSeconds(30),
                3L
        );
    }
}
