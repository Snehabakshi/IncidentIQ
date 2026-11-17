package com.incidentiq.backend.dto;


import java.util.UUID;

public record StoredSuggestion(UUID id, String answer,String SourceJson) {
}
