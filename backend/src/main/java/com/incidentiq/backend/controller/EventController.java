package com.incidentiq.backend.controller;

import com.incidentiq.backend.security.ApiKeyPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {
    @PostMapping
    public Map<String,Object>ingest(@AuthenticationPrincipal ApiKeyPrincipal principal){
        return Map.of("tenantId",principal.tenantId(),"apiKeyId",principal.apiKeyId());
    }
}
