package com.incidentiq.backend.controller;

import com.incidentiq.backend.dto.ApiKeyCreatedResponse;
import com.incidentiq.backend.dto.ApiKeyResponse;
import com.incidentiq.backend.dto.CreateApiKeyrequest;
import com.incidentiq.backend.service.ApiKeyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/api-keys")
public class ApiKeyController {
    private final ApiKeyService apiKeyService;
    public ApiKeyController(ApiKeyService apiKeyService){
        this.apiKeyService=apiKeyService;

    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiKeyCreatedResponse create(@Valid @RequestBody CreateApiKeyrequest request, @AuthenticationPrincipal  Jwt jwt){
        UUID tenantId=UUID.fromString(jwt.getClaimAsString("tenantId"));
        return apiKeyService.create(tenantId,request);

    }
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ApiKeyResponse> list(@AuthenticationPrincipal Jwt jwt) {
        return apiKeyService.list(UUID.fromString(jwt.getClaimAsString("tenantId")));
    }
    @PostMapping("/{id}/revoke")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void revoke(@PathVariable UUID id,@AuthenticationPrincipal Jwt jwt){
        apiKeyService.revoke(UUID.fromString(jwt.getClaimAsString("tenantId")),id);
    }
}
