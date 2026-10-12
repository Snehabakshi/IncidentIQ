package com.incidentiq.backend.dto;

public record TokenResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds

) {
}
