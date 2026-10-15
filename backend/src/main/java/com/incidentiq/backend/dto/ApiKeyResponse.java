package com.incidentiq.backend.dto;

import com.incidentiq.backend.entity.ApiKey;

import java.time.Instant;
import java.util.UUID;

public record ApiKeyResponse(
        UUID id,
        String name,
        String keyPrefix,
        Instant createdAt,
        Instant lastUsedAt,
        Instant revokedAt,
        String status

) {
    public static ApiKeyResponse from(ApiKey key){
        return new ApiKeyResponse(key.getId(),key.getName(),key.getKeyPrefix(),key.getCreatedAt(),key.getLastUsedAt(),key.getRevokedAt(),key.isRevoked()?"REVOKED":"ACTIVE");
    }
}
