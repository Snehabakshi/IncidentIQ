package com.incidentiq.backend.dto;

import java.time.Instant;
import java.util.UUID;

public record EventResponse(
        UUID id,
        String status,
        Instant receivedAt
) {
}
