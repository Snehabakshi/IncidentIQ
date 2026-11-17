package com.incidentiq.backend.exception;

import java.util.UUID;

public class SuggestionNotFoundException extends RuntimeException {
    public SuggestionNotFoundException(UUID suggestionId) {

        super("Incident not found:"+suggestionId);
    }
}
