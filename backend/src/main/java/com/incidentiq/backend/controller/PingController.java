package com.incidentiq.backend.controller;



import com.incidentiq.backend.dto.PingResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class PingController {
    private static final String SERVICE_NAME= "incidentIQ";

    @GetMapping("/ping")
    public PingResponse ping(){
        return new PingResponse("ping-pong",SERVICE_NAME);
    }

    @GetMapping("/hello")
    public PingResponse greet(){
        return new PingResponse("hello",SERVICE_NAME);
    }
}
