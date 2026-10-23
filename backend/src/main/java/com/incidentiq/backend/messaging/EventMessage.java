package com.incidentiq.backend.messaging;

import com.incidentiq.backend.entity.Severity;

import java.time.Instant;
import java.util.UUID;

public record EventMessage(
        UUID eventId,
        UUID tenantId,
        UUID apiKeyId,
        String service,
        Severity severity,
        String message,
        Instant occurredAt,
        Instant receivedAt,
        String externalId

) {
}
