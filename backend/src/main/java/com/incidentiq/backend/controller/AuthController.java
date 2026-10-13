package com.incidentiq.backend.controller;

import com.incidentiq.backend.dto.LoginRequest;
import com.incidentiq.backend.dto.SignUpRequest;
import com.incidentiq.backend.dto.SignUpResponse;
import com.incidentiq.backend.dto.TokenResponse;
import com.incidentiq.backend.service.AuthService;
import com.incidentiq.backend.service.SignUpService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final SignUpService signUpService;
    public AuthController(AuthService authService,SignUpService signUpService){

        this.authService=authService;
        this.signUpService=signUpService;
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request){
        return authService.login(request);
    }
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public SignUpResponse signUp(@Valid @RequestBody SignUpRequest request){
        return signUpService.signup(request);
    }
}
