package com.aydindemir.agent.domain.repository;

import com.aydindemir.agent.domain.model.Agent;
import com.aydindemir.agent.domain.model.AgentId;
import com.aydindemir.agent.domain.model.LicenseNumber;
import com.aydindemir.agent.domain.model.UserId;

import java.util.Optional;

public interface AgentRepository {

    Agent save(Agent agent);

    Optional<Agent> findById(AgentId id);

    boolean existsByLicenseNumber(LicenseNumber licenseNumber);

    boolean existsByUserId(UserId userId);
}
