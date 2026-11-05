package com.incidentiq.backend.service;

import com.incidentiq.backend.ai.EmbeddingClient;
import com.incidentiq.backend.ai.KnowledgeMatch;
import com.incidentiq.backend.entity.Incident;
import com.incidentiq.backend.repository.knowledgeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class knowledgeService {
    private final EmbeddingClient embeddingClient;
    private final knowledgeRepository knowledgeRepository;
    public knowledgeService(EmbeddingClient embeddingClient,knowledgeRepository knowledgeRepository){
        this.embeddingClient=embeddingClient;
        this.knowledgeRepository=knowledgeRepository;
    }
    public void indexResolvedToIncident(Incident incident){
        String problem= incident.getService() + ": "+incident.getTitle();
        String content=problem + ". Resolution: "+incident.getResolutionNote();
        knowledgeRepository.upsert(incident.getTenantId(),"INCIDENT",incident.getId(),truncate(problem,200),content, embeddingClient.embed(problem));
    }
    public List<KnowledgeMatch>findSimilar(UUID tenantId, String text, int limit){
        return knowledgeRepository.search(tenantId, embeddingClient.embed(text),limit );
    }
    private String truncate(String s , int max){
        return s.length()<=max ?s : s.substring(0,max);
    }
}
