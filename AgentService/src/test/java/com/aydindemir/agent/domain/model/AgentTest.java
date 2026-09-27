package com.aydindemir.agent.domain.model;

import com.aydindemir.agent.domain.exception.InvalidAgentStateException;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgentTest {

    private static final Instant CREATED_AT = Instant.parse("2026-09-27T18:00:00Z");
    private static final Clock CREATE_CLOCK = Clock.fixed(CREATED_AT, ZoneOffset.UTC);
    private static final Clock LATER_CLOCK = Clock.fixed(
            Instant.parse("2026-09-27T18:05:00Z"),
            ZoneOffset.UTC
    );

    @Test
    void createShouldStartActiveAndOffline() {
        Agent agent = newAgent();

        assertThat(agent.status()).isEqualTo(AgentStatus.ACTIVE);
        assertThat(agent.availability()).isEqualTo(AvailabilityStatus.OFFLINE);
        assertThat(agent.createdAt()).isEqualTo(CREATED_AT);
        assertThat(agent.updatedAt()).isEqualTo(CREATED_AT);
        assertThat(agent.version()).isZero();
    }

    @Test
    void activeAgentShouldChangeAvailability() {
        Agent agent = newAgent();

        agent.changeAvailability(AvailabilityStatus.AVAILABLE, LATER_CLOCK);

        assertThat(agent.availability()).isEqualTo(AvailabilityStatus.AVAILABLE);
        assertThat(agent.updatedAt()).isEqualTo(Instant.parse("2026-09-27T18:05:00Z"));
    }

    @Test
    void sameAvailabilityShouldBeNoOp() {
        Agent agent = newAgent();

        agent.changeAvailability(AvailabilityStatus.OFFLINE, LATER_CLOCK);

        assertThat(agent.updatedAt()).isEqualTo(CREATED_AT);
    }

    @Test
    void suspendShouldForceOffline() {
        Agent agent = newAgent();
        agent.changeAvailability(AvailabilityStatus.BUSY, LATER_CLOCK);

        Clock suspendClock = Clock.fixed(
                Instant.parse("2026-09-27T18:10:00Z"),
                ZoneOffset.UTC
        );
        agent.suspend(suspendClock);

        assertThat(agent.status()).isEqualTo(AgentStatus.SUSPENDED);
        assertThat(agent.availability()).isEqualTo(AvailabilityStatus.OFFLINE);
        assertThat(agent.updatedAt()).isEqualTo(Instant.parse("2026-09-27T18:10:00Z"));
    }

    @Test
    void suspendedAgentShouldRejectAvailableOrBusy() {
        Agent agent = newAgent();
        agent.suspend(LATER_CLOCK);

        assertThatThrownBy(() ->
                agent.changeAvailability(AvailabilityStatus.AVAILABLE, LATER_CLOCK))
                .isInstanceOf(InvalidAgentStateException.class);

        assertThatThrownBy(() ->
                agent.changeAvailability(AvailabilityStatus.BUSY, LATER_CLOCK))
                .isInstanceOf(InvalidAgentStateException.class);
    }

    @Test
    void activatingSuspendedAgentShouldReturnOffline() {
        Agent agent = newAgent();
        agent.changeAvailability(AvailabilityStatus.AVAILABLE, LATER_CLOCK);
        agent.suspend(LATER_CLOCK);

        Clock activationClock = Clock.fixed(
                Instant.parse("2026-09-27T18:15:00Z"),
                ZoneOffset.UTC
        );
        agent.activate(activationClock);

        assertThat(agent.status()).isEqualTo(AgentStatus.ACTIVE);
        assertThat(agent.availability()).isEqualTo(AvailabilityStatus.OFFLINE);
    }

    @Test
    void inactiveAgentShouldBeTerminal() {
        Agent agent = newAgent();
        agent.deactivate(LATER_CLOCK);

        assertThat(agent.status()).isEqualTo(AgentStatus.INACTIVE);
        assertThat(agent.availability()).isEqualTo(AvailabilityStatus.OFFLINE);

        assertThatThrownBy(() -> agent.activate(LATER_CLOCK))
                .isInstanceOf(InvalidAgentStateException.class);

        assertThatThrownBy(() -> agent.suspend(LATER_CLOCK))
                .isInstanceOf(InvalidAgentStateException.class);
    }

    @Test
    void reconstituteShouldPreservePersistedStateAndVersion() {
        AgentId id = new AgentId(UUID.randomUUID());
        UserId userId = new UserId(UUID.randomUUID());
        Instant updatedAt = Instant.parse("2026-09-27T18:30:00Z");

        Agent agent = Agent.reconstitute(
                id,
                userId,
                new LicenseNumber("LIC-42"),
                new AgencyInfo("North Realty", null, null),
                AgentStatus.ACTIVE,
                AvailabilityStatus.BUSY,
                CREATED_AT,
                updatedAt,
                7L
        );

        assertThat(agent.id()).isEqualTo(id);
        assertThat(agent.userId()).isEqualTo(userId);
        assertThat(agent.status()).isEqualTo(AgentStatus.ACTIVE);
        assertThat(agent.availability()).isEqualTo(AvailabilityStatus.BUSY);
        assertThat(agent.createdAt()).isEqualTo(CREATED_AT);
        assertThat(agent.updatedAt()).isEqualTo(updatedAt);
        assertThat(agent.version()).isEqualTo(7L);
    }

    @Test
    void reconstituteShouldRejectInvalidPersistedState() {
        assertThatThrownBy(() -> Agent.reconstitute(
                new AgentId(UUID.randomUUID()),
                new UserId(UUID.randomUUID()),
                new LicenseNumber("LIC-42"),
                new AgencyInfo("North Realty", null, null),
                AgentStatus.SUSPENDED,
                AvailabilityStatus.AVAILABLE,
                CREATED_AT,
                CREATED_AT,
                1L
        )).isInstanceOf(InvalidAgentStateException.class);
    }

    private Agent newAgent() {
        return Agent.create(
                new UserId(UUID.randomUUID()),
                new LicenseNumber("LIC-42"),
                new AgencyInfo("North Realty", "REG-1", "+90 555 000 00 00"),
                CREATE_CLOCK
        );
    }
}
