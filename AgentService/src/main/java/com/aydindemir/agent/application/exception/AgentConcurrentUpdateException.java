package com.aydindemir.agent.application.exception;

import com.aydindemir.agent.domain.model.AgentId;

public class AgentConcurrentUpdateException extends RuntimeException {

    public AgentConcurrentUpdateException(AgentId agentId, Throwable cause) {
        super("Agent was updated concurrently: " + agentId.value(), cause);
    }
}
