package com.aydindemir.seller.presentation.rest.response;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
        String code,
        String message,
        int status,
        Instant timestamp,
        List<String> details) {

    public ApiErrorResponse {
        details = details == null ? List.of() : List.copyOf(details);
    }

    public static ApiErrorResponse of(
            String code,
            String message,
            int status,
            List<String> details) {
        return new ApiErrorResponse(
                code,
                message,
                status,
                Instant.now(),
                details);
    }
}
