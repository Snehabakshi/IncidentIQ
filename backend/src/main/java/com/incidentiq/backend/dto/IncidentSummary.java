package com.incidentiq.backend.dto;

import com.incidentiq.backend.entity.Incident;
import com.incidentiq.backend.entity.IncidentStatus;
import com.incidentiq.backend.entity.Severity;

import java.time.Instant;
import java.util.UUID;

public record IncidentSummary(
        UUID id,
        String service,
        String title,
        Severity severity,
        IncidentStatus status,
        int eventCount,
        Instant firstSeenAt,
        Instant lastSeenAt,
        Instant resolvedAt

) {
    public static IncidentSummary from(Incident i){
        return new IncidentSummary(i.getId(),i.getService(),i.getTitle(),i.getSeverity(),i.getStatus(),i.getEventCount(),i.getFirstSeenAt(),i.getLastSeenAt(),i.getResolvedAt());
    }
}
