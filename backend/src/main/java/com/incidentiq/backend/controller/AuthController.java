package com.incidentiq.backend.controller;

import com.incidentiq.backend.dto.LoginRequest;
import com.incidentiq.backend.dto.RegisterRequest;
import com.incidentiq.backend.dto.TokenResponse;
import com.incidentiq.backend.dto.UserResponse;
import com.incidentiq.backend.service.AuthService;
import com.incidentiq.backend.service.UserService;
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
    private final UserService userService;
    private final AuthService authService;
    public AuthController(UserService userService,AuthService authService){
        this.userService=userService;
        this.authService=authService;

    }
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request){
        return userService.register(request);
    }
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request){
        return authService.login(request);
    }
}
