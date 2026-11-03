package com.incidentiq.backend.ai;

public interface EmbeddingClient {
    float [] embed(String text);
    int dimensions();
}
