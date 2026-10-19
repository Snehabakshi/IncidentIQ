package com.incidentiq.backend.dto;

import com.incidentiq.backend.entity.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateEventRequest(
        @NotBlank(message="service is required")
        @Size(max=100, message="service must be atmost 100 characters")
        String service,

        @NotNull(message="severity is required")
        Severity severity,

        @NotBlank(message="message is required")
        @Size(max=10000,message="message must be at most 10000 characters")
        String message,

        Instant occurredAt,

        @Size(max=200, message="externalId must be at most 200 characters")
        String externalId
) {
}
