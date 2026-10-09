package com.incidentiq.backend.dto;

import com.incidentiq.backend.entity.Tenant;

import java.time.Instant;
import java.util.UUID;

public record TenantResponse(
        UUID id,
        String name,
        Instant createdAt
) {
    public static TenantResponse from(Tenant tenant){
        return new TenantResponse(
                tenant.getId(),
                tenant.getName(),
                tenant.getCreatedAt()
        );
    }
}
