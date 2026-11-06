package com.incidentiq.backend.service;

import com.incidentiq.backend.ai.EmbeddingClient;
import com.incidentiq.backend.ai.KnowledgeMatch;
import com.incidentiq.backend.ai.RunBook;
import com.incidentiq.backend.ai.RunBookCatalog;
import com.incidentiq.backend.entity.Incident;
import com.incidentiq.backend.repository.KnowledgeRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
public class KnowledgeService {
    private final EmbeddingClient embeddingClient;
    private final KnowledgeRepository knowledgeRepository;
    public KnowledgeService(EmbeddingClient embeddingClient, KnowledgeRepository knowledgeRepository){
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
    public void indexRunbook(UUID tenantId, RunBook runbook){
        UUID sourceId=UUID.nameUUIDFromBytes(("runbook:" + runbook.slug()).getBytes(StandardCharsets.UTF_8));
        String text=runbook.title()+". "+ runbook.content();
        knowledgeRepository.upsert(tenantId,"RUNBOOK",sourceId,runbook.title(),text, embeddingClient.embed(text));

    }
    public void seedRunbooksIfMissing(UUID tenantId){
        if(knowledgeRepository.countByType(tenantId,"RUNBOOK")>= RunBookCatalog.ALL.size()){
            return;
        }
        for(RunBook runBook:RunBookCatalog.ALL){
            indexRunbook(tenantId,runBook);
        }
    }
}
