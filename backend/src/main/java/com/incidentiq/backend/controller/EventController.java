package com.incidentiq.backend.controller;

import com.incidentiq.backend.dto.CreateEventRequest;
import com.incidentiq.backend.dto.EventResponse;
import com.incidentiq.backend.dto.EventSummary;
import com.incidentiq.backend.dto.PageResponse;
import com.incidentiq.backend.security.ApiKeyPrincipal;
import com.incidentiq.backend.service.EventQueryService;
import com.incidentiq.backend.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {
    private final EventService eventService;
    private final EventQueryService eventQueryService;

    public EventController(EventService eventService, EventQueryService eventQueryService){
        this.eventService=eventService;
        this.eventQueryService=eventQueryService;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public EventResponse ingest(@Valid @RequestBody CreateEventRequest request, @AuthenticationPrincipal ApiKeyPrincipal principal){
        return eventService.accept(principal,request);
    }
    @GetMapping
    public PageResponse<EventSummary>list(@RequestParam(required=false) String service,@RequestParam(defaultValue = "0")int page,@RequestParam(defaultValue = "50")int size,@AuthenticationPrincipal Jwt jwt){
        UUID tenantId=UUID.fromString(jwt.getClaimAsString("tenantId"));
        return eventQueryService.list(tenantId,service,page,size);
    }
}
