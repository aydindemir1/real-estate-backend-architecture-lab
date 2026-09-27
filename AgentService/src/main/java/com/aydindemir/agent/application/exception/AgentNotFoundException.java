package com.aydindemir.agent.application.exception;

import com.aydindemir.agent.domain.model.AgentId;

public class AgentNotFoundException extends RuntimeException {

    public AgentNotFoundException(AgentId agentId) {
        super("Agent not found: " + agentId.value());
    }
}
