package com.incidentiq.backend.controller;

import com.incidentiq.backend.dto.MeResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class MeController {
    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal Jwt jwt){
        return new MeResponse(
                jwt.getSubject(),
                jwt.getClaimAsString("tenantId"),
                jwt.getClaimAsString("role")
        );
    }
}
