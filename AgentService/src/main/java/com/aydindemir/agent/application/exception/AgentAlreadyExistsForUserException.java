package com.aydindemir.agent.application.exception;

import com.aydindemir.agent.domain.model.UserId;

public class AgentAlreadyExistsForUserException extends RuntimeException {

    public AgentAlreadyExistsForUserException(UserId userId) {
        super("Agent already exists for user: " + userId.value());
    }
}
