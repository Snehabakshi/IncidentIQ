package com.incidentiq.backend.exception;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException(String email){
        super("Email Already registered:"+email);
    }
}
