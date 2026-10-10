package com.incidentiq.backend.exception;

import java.util.UUID;

public class TenantNotFoundException extends RuntimeException {
    public TenantNotFoundException(UUID id){
        super("Tenant Not Found:"+id);
    }
}
