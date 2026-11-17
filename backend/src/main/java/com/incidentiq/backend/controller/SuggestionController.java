package com.incidentiq.backend.controller;

import com.incidentiq.backend.dto.FeedbackRequest;
import com.incidentiq.backend.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/suggestions")
public class SuggestionController {
    private final FeedbackService feedbackService;

    public SuggestionController(FeedbackService feedbackService){
        this.feedbackService=feedbackService;
    }
    @PostMapping("/{id}/feedback")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void feedback(@PathVariable UUID id, @Valid @RequestBody FeedbackRequest request, @AuthenticationPrincipal Jwt jwt){
        UUID tenantId=UUID.fromString(jwt.getClaimAsString("tenantId"));
        UUID userId=UUID.fromString(jwt.getSubject());
        feedbackService.submit(tenantId,userId,id,request);
    }

}
