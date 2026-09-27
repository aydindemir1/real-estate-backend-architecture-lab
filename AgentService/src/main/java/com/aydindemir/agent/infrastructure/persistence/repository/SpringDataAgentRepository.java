package com.aydindemir.agent.infrastructure.persistence.repository;

import com.aydindemir.agent.infrastructure.persistence.entity.AgentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataAgentRepository extends JpaRepository<AgentJpaEntity, UUID> {

    boolean existsByUserId(UUID userId);

    boolean existsByLicenseNumber(String licenseNumber);
}
