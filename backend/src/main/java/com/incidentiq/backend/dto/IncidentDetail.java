package com.incidentiq.backend.dto;
import com.incidentiq.backend.entity.Incident;
import com.incidentiq.backend.entity.IncidentStatus;
import com.incidentiq.backend.entity.Severity;

import java.time.Instant;
import java.util.UUID;
public record IncidentDetail(
        UUID id,
        String service,
        String title,
        Severity severity,
        IncidentStatus status,
        int eventCount,
        Instant firstSeenAt,
        Instant lastSeenAt,
        Instant resolvedAt,
        UUID resolvedBy,
        String resolutionNote

) {
    public static IncidentDetail from(Incident i) {
        return new IncidentDetail(i.getId(), i.getService(), i.getTitle(), i.getSeverity(),
                i.getStatus(), i.getEventCount(), i.getFirstSeenAt(), i.getLastSeenAt(),
                i.getResolvedAt(), i.getResolvedBy(), i.getResolutionNote());
    }
}
