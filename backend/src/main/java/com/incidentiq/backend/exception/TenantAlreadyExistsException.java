package com.incidentiq.backend.exception;

public class TenantAlreadyExistsException extends RuntimeException{
    public TenantAlreadyExistsException(String name){
        super("Tenant Already Exists :" +name);
    }
}
