package com.aydindemir.agent.infrastructure.persistence.adapter;

import com.aydindemir.agent.application.exception.AgentAlreadyExistsForUserException;
import com.aydindemir.agent.application.exception.AgentConcurrentUpdateException;
import com.aydindemir.agent.application.exception.DuplicateLicenseNumberException;
import com.aydindemir.agent.domain.model.Agent;
import com.aydindemir.agent.domain.model.AgentId;
import com.aydindemir.agent.domain.model.LicenseNumber;
import com.aydindemir.agent.domain.model.UserId;
import com.aydindemir.agent.domain.repository.AgentRepository;
import com.aydindemir.agent.infrastructure.persistence.entity.AgentJpaEntity;
import com.aydindemir.agent.infrastructure.persistence.mapper.AgentPersistenceMapper;
import com.aydindemir.agent.infrastructure.persistence.repository.SpringDataAgentRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AgentRepositoryAdapter implements AgentRepository {

    private static final String USER_UNIQUE_CONSTRAINT = "uk_agents_user_id";
    private static final String LICENSE_UNIQUE_CONSTRAINT = "uk_agents_license_number";

    private final SpringDataAgentRepository springDataRepository;
    private final AgentPersistenceMapper mapper;

    public AgentRepositoryAdapter(
            SpringDataAgentRepository springDataRepository,
            AgentPersistenceMapper mapper
    ) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Agent save(Agent agent) {
        boolean existing = springDataRepository.existsById(agent.id().value());

        AgentJpaEntity entity = existing
                ? mapper.toEntity(agent)
                : mapper.toNewEntity(agent);

        try {
            AgentJpaEntity savedEntity = springDataRepository.saveAndFlush(entity);
            return mapper.toDomain(savedEntity);
        } catch (ObjectOptimisticLockingFailureException exception) {
            throw new AgentConcurrentUpdateException(agent.id(), exception);
        } catch (DataIntegrityViolationException exception) {
            throw translateConstraintViolation(agent, exception);
        }
    }

    @Override
    public Optional<Agent> findById(AgentId id) {
        return springDataRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByLicenseNumber(LicenseNumber licenseNumber) {
        return springDataRepository.existsByLicenseNumber(licenseNumber.value());
    }

    @Override
    public boolean existsByUserId(UserId userId) {
        return springDataRepository.existsByUserId(userId.value());
    }

    private RuntimeException translateConstraintViolation(
            Agent agent,
            DataIntegrityViolationException exception
    ) {
        ConstraintViolationException constraintViolation = findConstraintViolation(exception);

        if (constraintViolation == null) {
            return exception;
        }

        String constraintName = constraintViolation.getConstraintName();

        if (matchesConstraint(constraintName, USER_UNIQUE_CONSTRAINT)) {
            return new AgentAlreadyExistsForUserException(agent.userId());
        }

        if (matchesConstraint(constraintName, LICENSE_UNIQUE_CONSTRAINT)) {
            return new DuplicateLicenseNumberException(agent.licenseNumber());
        }

        return exception;
    }

    private boolean matchesConstraint(String actualConstraintName, String expectedConstraintName) {
        if (actualConstraintName == null) {
            return false;
        }

        return actualConstraintName.equalsIgnoreCase(expectedConstraintName)
                || actualConstraintName.toLowerCase()
                .endsWith("." + expectedConstraintName.toLowerCase());
    }

    private ConstraintViolationException findConstraintViolation(Throwable throwable) {
        Throwable current = throwable;

        while (current != null) {
            if (current instanceof ConstraintViolationException constraintViolation) {
                return constraintViolation;
            }
            current = current.getCause();
        }

        return null;
    }
}
