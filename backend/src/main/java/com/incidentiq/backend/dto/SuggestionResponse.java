package com.incidentiq.backend.dto;

import java.util.List;
import java.util.UUID;

public record SuggestionResponse(UUID suggestionId, String answer, List<SuggestionSource>sources) {
}
