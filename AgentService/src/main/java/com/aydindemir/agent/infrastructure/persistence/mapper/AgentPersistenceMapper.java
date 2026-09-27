package com.aydindemir.agent.infrastructure.persistence.mapper;

import com.aydindemir.agent.domain.model.AgencyInfo;
import com.aydindemir.agent.domain.model.Agent;
import com.aydindemir.agent.domain.model.AgentId;
import com.aydindemir.agent.domain.model.LicenseNumber;
import com.aydindemir.agent.domain.model.UserId;
import com.aydindemir.agent.infrastructure.persistence.entity.AgentJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AgentPersistenceMapper {

    public AgentJpaEntity toNewEntity(Agent agent) {
        return toEntity(agent, null);
    }

    public AgentJpaEntity toEntity(Agent agent) {
        return toEntity(agent, agent.version());
    }

    public Agent toDomain(AgentJpaEntity entity) {
        return Agent.reconstitute(
                new AgentId(entity.getId()),
                new UserId(entity.getUserId()),
                new LicenseNumber(entity.getLicenseNumber()),
                new AgencyInfo(
                        entity.getAgencyName(),
                        entity.getAgencyRegistrationNumber(),
                        entity.getOfficePhone()
                ),
                entity.getStatus(),
                entity.getAvailabilityStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }

    private AgentJpaEntity toEntity(Agent agent, Long version) {
        return new AgentJpaEntity(
                agent.id().value(),
                agent.userId().value(),
                agent.licenseNumber().value(),
                agent.agencyInfo().agencyName(),
                agent.agencyInfo().registrationNumber(),
                agent.agencyInfo().officePhone(),
                agent.status(),
                agent.availability(),
                agent.createdAt(),
                agent.updatedAt(),
                version
        );
    }
}
