package com.incidentiq.backend.ai;

import java.util.UUID;

public record KnowledgeMatch(
        UUID id,
        String sourceType,
        UUID sourceId,
        String title,
        String content,
        double distance
) {
}
