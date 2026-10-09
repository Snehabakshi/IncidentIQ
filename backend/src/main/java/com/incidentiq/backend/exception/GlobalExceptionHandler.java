package com.incidentiq.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(TenantAlreadyExistsException.class)
    public ProblemDetail handleTenantExists(TenantAlreadyExistsException ex){
        ProblemDetail problem =ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,ex.getMessage());
        problem.setTitle("Tenant Already Exists");
        return problem;
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation ( MethodArgumentNotValidException ex){
        Map<String,String> errors=new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(),error.getDefaultMessage()));
        ProblemDetail problem =ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,"Validation Failed");
        problem.setTitle("Invalid request");
        problem.setProperty("errors",errors);
        return problem;
    }

}
