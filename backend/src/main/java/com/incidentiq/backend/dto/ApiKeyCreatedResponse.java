package com.incidentiq.backend.dto;

import java.time.Instant;
import java.util.UUID;

public record ApiKeyCreatedResponse(
        UUID id,
        String name,
        String keyPrefix,
        String apiKey,
        Instant createdAt
) {
}
