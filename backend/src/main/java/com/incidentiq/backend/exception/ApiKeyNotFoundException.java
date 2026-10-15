package com.incidentiq.backend.exception;

import com.incidentiq.backend.entity.ApiKey;

import java.util.UUID;

public class ApiKeyNotFoundException extends RuntimeException {
    public ApiKeyNotFoundException(UUID keyId){
        super("API key not found: " + keyId);
    }
}
