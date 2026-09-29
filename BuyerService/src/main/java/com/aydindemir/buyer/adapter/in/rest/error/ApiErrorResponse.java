package com.aydindemir.buyer.adapter.in.rest.error;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        String correlationId,
        String traceId,
        List<FieldErrorDetail> errors
) {

    public ApiErrorResponse {
        errors = errors == null ? List.of() : List.copyOf(errors);
    }

    public record FieldErrorDetail(
            String field,
            String message
    ) {
    }
}
