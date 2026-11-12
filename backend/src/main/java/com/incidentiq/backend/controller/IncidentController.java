package com.incidentiq.backend.controller;

import com.incidentiq.backend.ai.KnowledgeMatch;
import com.incidentiq.backend.dto.EventSummary;
import com.incidentiq.backend.dto.IncidentDetail;
import com.incidentiq.backend.dto.IncidentSummary;
import com.incidentiq.backend.dto.PageResponse;
import com.incidentiq.backend.dto.ResolveIncidentRequest;
import com.incidentiq.backend.dto.SuggestionResponse;
import com.incidentiq.backend.entity.IncidentStatus;
import com.incidentiq.backend.service.IncidentQueryService;
import com.incidentiq.backend.service.IncidentService;
import com.incidentiq.backend.service.SuggestionService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {
    private final IncidentQueryService incidentQueryService;
    private final IncidentService incidentService;
    private final SuggestionService suggestionService;
    public IncidentController(IncidentQueryService incidentQueryService, IncidentService incidentService, SuggestionService suggestionService){
        this.incidentQueryService=incidentQueryService;
        this.incidentService=incidentService;
        this.suggestionService=suggestionService;
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
    @GetMapping("/{id}")
    public IncidentDetail get(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt){
        UUID tenantId=UUID.fromString(jwt.getClaimAsString("tenantId"));
        return incidentQueryService.get(tenantId, id);
    }
    @GetMapping("/{id}/events")
    public PageResponse<EventSummary> events(@PathVariable UUID id,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "50") int size,
                                             @AuthenticationPrincipal Jwt jwt) {
        UUID tenantId = UUID.fromString(jwt.getClaimAsString("tenantId"));
        return incidentQueryService.listEvents(tenantId, id, page, size);
    }
    @PostMapping("/{id}/resolve")
    public IncidentDetail resolve(@PathVariable UUID id, @Valid @RequestBody ResolveIncidentRequest request,@AuthenticationPrincipal Jwt jwt){
        UUID tenantId=UUID.fromString(jwt.getClaimAsString("tenantId"));
        UUID userId=UUID.fromString(jwt.getSubject());
        return incidentService.resolve(tenantId,userId,id,request.note());
    }
    @GetMapping("/{id}/similar")
    public List<KnowledgeMatch> similar(@PathVariable UUID id,@RequestParam (defaultValue = "3") int limit, @AuthenticationPrincipal Jwt jwt){
        UUID tenantId=UUID.fromString(jwt.getClaimAsString("tenantId"));
        return incidentQueryService.similar(tenantId,id,limit);
    }
    @PostMapping("/{id}/suggestion")
    public SuggestionResponse suggestion(@PathVariable UUID id,@AuthenticationPrincipal Jwt jwt){
        UUID tenantId=UUID.fromString(jwt.getClaimAsString("tenantId"));
        return suggestionService.suggest(tenantId,id);
    }
}
