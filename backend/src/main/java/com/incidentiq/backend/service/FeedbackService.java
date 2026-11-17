package com.incidentiq.backend.service;

import com.incidentiq.backend.dto.FeedbackRequest;
import com.incidentiq.backend.exception.SuggestionNotFoundException;
import com.incidentiq.backend.repository.FeedbackRepository;
import com.incidentiq.backend.repository.SuggestionRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FeedbackService {
    private final SuggestionRepository suggestionRepository;
    private final FeedbackRepository feedbackRepository;
    public FeedbackService(SuggestionRepository suggestionRepository, FeedbackRepository feedbackRepository){
        this.suggestionRepository=suggestionRepository;
        this.feedbackRepository=feedbackRepository;
    }
    public void submit(UUID tenantId, UUID userId, UUID suggestionId, FeedbackRequest request){
        if(!suggestionRepository.existsForTenant(tenantId,suggestionId)){
            throw new SuggestionNotFoundException(suggestionId);
        }
        String comment=(request.comment() == null || request.comment().isBlank())?null : request.comment().trim();
        feedbackRepository.upsert(tenantId,suggestionId,userId,request.rating().name(),comment);
    }
}
