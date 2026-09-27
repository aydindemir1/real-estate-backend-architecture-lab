package com.aydindemir.agent.infrastructure.persistence;

import com.aydindemir.agent.application.exception.AgentAlreadyExistsForUserException;
import com.aydindemir.agent.application.exception.DuplicateLicenseNumberException;
import com.aydindemir.agent.domain.model.AgencyInfo;
import com.aydindemir.agent.domain.model.Agent;
import com.aydindemir.agent.domain.model.AgentStatus;
import com.aydindemir.agent.domain.model.AvailabilityStatus;
import com.aydindemir.agent.domain.model.LicenseNumber;
import com.aydindemir.agent.domain.model.UserId;
import com.aydindemir.agent.infrastructure.persistence.adapter.AgentRepositoryAdapter;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgentRepositoryAdapterIntegrationTest extends MySqlPersistenceTestSupport {

    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-09-27T18:00:00Z"),
            ZoneOffset.UTC
    );

    @Autowired
    private AgentRepositoryAdapter repository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void flywayShouldCreateAgentsTableAndSchemaHistory() {
        Integer agentsTableCount = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = DATABASE()
                  AND table_name = 'agents'
                """,
                Integer.class
        );

        Integer flywayHistoryCount = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = DATABASE()
                  AND table_name = 'flyway_schema_history'
                """,
                Integer.class
        );

        assertThat(agentsTableCount).isEqualTo(1);
        assertThat(flywayHistoryCount).isEqualTo(1);
    }

    @Test
    void saveAndFindShouldRoundTripAggregateThroughMySql() {
        Agent original = newAgent(UUID.randomUUID(), "LIC-100");

        Agent saved = repository.save(original);
        entityManager.clear();

        Agent reloaded = repository.findById(saved.id()).orElseThrow();

        assertThat(saved.version()).isZero();
        assertThat(reloaded.id()).isEqualTo(saved.id());
        assertThat(reloaded.userId()).isEqualTo(original.userId());
        assertThat(reloaded.licenseNumber()).isEqualTo(new LicenseNumber("LIC-100"));
        assertThat(reloaded.agencyInfo()).isEqualTo(
                new AgencyInfo("North Realty", "REG-100", "+90 555 111 22 33")
        );
        assertThat(reloaded.status()).isEqualTo(AgentStatus.ACTIVE);
        assertThat(reloaded.availability()).isEqualTo(AvailabilityStatus.OFFLINE);
        assertThat(reloaded.createdAt()).isEqualTo(Instant.parse("2026-09-27T18:00:00Z"));
        assertThat(reloaded.updatedAt()).isEqualTo(Instant.parse("2026-09-27T18:00:00Z"));
        assertThat(reloaded.version()).isZero();
    }

    @Test
    void databaseShouldEnforceOneAgentPerUser() {
        UUID userId = UUID.randomUUID();
        repository.save(newAgent(userId, "LIC-101"));

        assertThatThrownBy(() -> repository.save(newAgent(userId, "LIC-102")))
                .isInstanceOf(AgentAlreadyExistsForUserException.class);
    }

    @Test
    void databaseShouldEnforceUniqueLicenseNumber() {
        repository.save(newAgent(UUID.randomUUID(), "LIC-103"));

        assertThatThrownBy(() -> repository.save(newAgent(UUID.randomUUID(), "LIC-103")))
                .isInstanceOf(DuplicateLicenseNumberException.class);
    }

    @Test
    void repositoryExistenceChecksShouldUsePersistedValues() {
        UUID userId = UUID.randomUUID();
        Agent saved = repository.save(newAgent(userId, "  LIC-104  "));
        entityManager.clear();

        assertThat(repository.existsByUserId(new UserId(userId))).isTrue();
        assertThat(repository.existsByLicenseNumber(new LicenseNumber("LIC-104"))).isTrue();
        assertThat(saved.licenseNumber()).isEqualTo(new LicenseNumber("LIC-104"));
    }

    private Agent newAgent(UUID userId, String licenseNumber) {
        return Agent.create(
                new UserId(userId),
                new LicenseNumber(licenseNumber),
                new AgencyInfo("North Realty", "REG-100", "+90 555 111 22 33"),
                CLOCK
        );
    }
}
