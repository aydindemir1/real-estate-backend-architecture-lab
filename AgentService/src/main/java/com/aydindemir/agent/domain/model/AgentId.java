package com.aydindemir.agent.domain.model;

import java.util.UUID;

public record AgentId(UUID value) {

    public AgentId {
        if (value == null) {
            throw new IllegalArgumentException("Agent id must not be null");
        }
    }
}
