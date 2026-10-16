package com.incidentiq.backend.security;

import java.util.UUID;

public record ApiKeyPrincipal(UUID tenantId,UUID apiKeyId) {
}
