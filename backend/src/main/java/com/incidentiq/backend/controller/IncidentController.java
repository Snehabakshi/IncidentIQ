package com.incidentiq.backend.controller;

import com.incidentiq.backend.dto.IncidentSummary;
import com.incidentiq.backend.dto.PageResponse;
import com.incidentiq.backend.entity.IncidentStatus;
import com.incidentiq.backend.service.IncidentQueryService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {
    private final IncidentQueryService incidentQueryService;
    public IncidentController(IncidentQueryService incidentQueryService){
        this.incidentQueryService=incidentQueryService;
    }
    @GetMapping
    public PageResponse<IncidentSummary>list(
            @RequestParam(required = false) IncidentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @AuthenticationPrincipal Jwt jwt
    ){
        UUID tenantId=UUID.fromString(jwt.getClaimAsString("tenantId"));
        return incidentQueryService.list(tenantId, status, page, size);
    }

}
