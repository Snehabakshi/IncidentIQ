package com.incidentiq.backend.dto;

public record SignUpResponse(TenantResponse tenant, UserResponse admin) {
}
