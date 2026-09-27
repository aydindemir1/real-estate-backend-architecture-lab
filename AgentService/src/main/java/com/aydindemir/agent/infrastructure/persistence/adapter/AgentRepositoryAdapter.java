package com.aydindemir.agent.infrastructure.persistence.adapter;

import com.aydindemir.agent.domain.model.Agent;
import com.aydindemir.agent.domain.model.AgentId;
import com.aydindemir.agent.domain.model.LicenseNumber;
import com.aydindemir.agent.domain.model.UserId;
import com.aydindemir.agent.domain.repository.AgentRepository;
import com.aydindemir.agent.infrastructure.persistence.entity.AgentJpaEntity;
import com.aydindemir.agent.infrastructure.persistence.mapper.AgentPersistenceMapper;
import com.aydindemir.agent.infrastructure.persistence.repository.SpringDataAgentRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AgentRepositoryAdapter implements AgentRepository {

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

        AgentJpaEntity savedEntity = springDataRepository.saveAndFlush(entity);
        return mapper.toDomain(savedEntity);
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
}
