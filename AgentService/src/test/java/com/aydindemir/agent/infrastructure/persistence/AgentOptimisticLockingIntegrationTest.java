package com.aydindemir.agent.infrastructure.persistence;

import com.aydindemir.agent.application.exception.AgentConcurrentUpdateException;
import com.aydindemir.agent.domain.model.AgencyInfo;
import com.aydindemir.agent.domain.model.Agent;
import com.aydindemir.agent.domain.model.AvailabilityStatus;
import com.aydindemir.agent.domain.model.LicenseNumber;
import com.aydindemir.agent.domain.model.UserId;
import com.aydindemir.agent.infrastructure.persistence.adapter.AgentRepositoryAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgentOptimisticLockingIntegrationTest extends MySqlPersistenceTestSupport {

    private static final Clock CREATE_CLOCK = Clock.fixed(
            Instant.parse("2026-09-27T18:00:00Z"),
            ZoneOffset.UTC
    );

    private static final Clock FIRST_UPDATE_CLOCK = Clock.fixed(
            Instant.parse("2026-09-27T18:05:00Z"),
            ZoneOffset.UTC
    );

    private static final Clock STALE_UPDATE_CLOCK = Clock.fixed(
            Instant.parse("2026-09-27T18:10:00Z"),
            ZoneOffset.UTC
    );

    @Autowired
    private AgentRepositoryAdapter repository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void staleUpdateShouldBeRejectedByOptimisticLocking() {
        TransactionTemplate transaction = new TransactionTemplate(transactionManager);

        Agent created = transaction.execute(status ->
                repository.save(newAgent())
        );

        assertThat(created).isNotNull();

        Agent firstSnapshot = transaction.execute(status ->
                repository.findById(created.id()).orElseThrow()
        );

        Agent staleSnapshot = transaction.execute(status ->
                repository.findById(created.id()).orElseThrow()
        );

        assertThat(firstSnapshot).isNotNull();
        assertThat(staleSnapshot).isNotNull();
        assertThat(firstSnapshot.version()).isZero();
        assertThat(staleSnapshot.version()).isZero();

        firstSnapshot.changeAvailability(
                AvailabilityStatus.AVAILABLE,
                FIRST_UPDATE_CLOCK
        );

        Agent firstUpdate = transaction.execute(status ->
                repository.save(firstSnapshot)
        );

        assertThat(firstUpdate).isNotNull();
        assertThat(firstUpdate.version()).isEqualTo(1L);
        assertThat(firstUpdate.availability()).isEqualTo(AvailabilityStatus.AVAILABLE);

        staleSnapshot.changeAvailability(
                AvailabilityStatus.BUSY,
                STALE_UPDATE_CLOCK
        );

        assertThatThrownBy(() ->
                transaction.execute(status -> repository.save(staleSnapshot))
        ).isInstanceOf(AgentConcurrentUpdateException.class);

        Agent persisted = transaction.execute(status ->
                repository.findById(created.id()).orElseThrow()
        );

        assertThat(persisted).isNotNull();
        assertThat(persisted.version()).isEqualTo(1L);
        assertThat(persisted.availability()).isEqualTo(AvailabilityStatus.AVAILABLE);
        assertThat(persisted.updatedAt()).isEqualTo(
                Instant.parse("2026-09-27T18:05:00Z")
        );
    }

    private Agent newAgent() {
        return Agent.create(
                new UserId(UUID.randomUUID()),
                new LicenseNumber("LIC-OPT-1"),
                new AgencyInfo("North Realty", "REG-OPT-1", null),
                CREATE_CLOCK
        );
    }
}
