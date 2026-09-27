package com.aydindemir.agent.presentation.error;

import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

public record AgentErrorResponse(
        String code,
        String message,
        List<String> fields,
        Instant timestamp,
        HttpStatus status
) {
}
