package com.aydindemir.agent.domain.model;

import java.util.UUID;

public record UserId(UUID value) {

    public UserId {
        if (value == null) {
            throw new IllegalArgumentException("User id must not be null");
        }
    }
}
