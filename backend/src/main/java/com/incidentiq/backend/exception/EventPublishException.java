package com.incidentiq.backend.exception;

public class EventPublishException extends RuntimeException{
    public EventPublishException(String message,Throwable cause){
        super(message,cause);
    }
}
