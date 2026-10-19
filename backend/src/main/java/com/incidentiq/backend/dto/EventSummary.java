package com.incidentiq.backend.dto;

import com.incidentiq.backend.entity.Event;
import com.incidentiq.backend.entity.Severity;

import java.time.Instant;
import java.util.UUID;

public record EventSummary(
        UUID id,
        String service,
        Severity severity,
        String message,
        Instant occurredAt,
        Instant receivedAt,
        String externalId,
        UUID incidentId
) {
    public static EventSummary from(Event e){
      return new EventSummary(e.getId(),e.getService(),e.getSeverity(),e.getMessage(),e.getOccurredAt(),e.getReceivedAt(),e.getExternalId(),e.getIncidentId());
    }
}
