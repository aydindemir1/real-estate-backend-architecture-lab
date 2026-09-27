package com.aydindemir.agent.presentation.error;

import java.time.Instant;
import java.util.List;

public record AgentErrorResponse(
        String code,
        String message,
        List<String> fields,
        Instant timestamp,
        String status
) {
}
