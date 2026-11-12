package com.incidentiq.backend.ai;

public interface LlmClient {
    String complete(String prompt);
}
