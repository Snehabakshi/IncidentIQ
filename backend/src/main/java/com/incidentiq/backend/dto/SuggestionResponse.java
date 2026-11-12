package com.incidentiq.backend.dto;

import java.util.List;

public record SuggestionResponse(String answer, List<SuggestionSource>sources) {
}
