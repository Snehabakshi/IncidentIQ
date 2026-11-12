package com.incidentiq.backend.service;

import com.incidentiq.backend.ai.KnowledgeMatch;
import com.incidentiq.backend.ai.LlmClient;
import com.incidentiq.backend.dto.IncidentDetail;
import com.incidentiq.backend.dto.SuggestionResponse;
import com.incidentiq.backend.dto.SuggestionSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SuggestionService {
    private static final int MAX_SOURCES=5;
    private final LlmClient llmClient;
    private final IncidentQueryService incidentQueryService;
    public SuggestionService(IncidentQueryService incidentQueryService, LlmClient llmClient){
        this.incidentQueryService=incidentQueryService;
        this.llmClient=llmClient;

    }
    public SuggestionResponse suggest(UUID tenantId,UUID incidentId){
        IncidentDetail incident=incidentQueryService.get(tenantId, incidentId);
        List<KnowledgeMatch> matches=incidentQueryService.similar(tenantId,incidentId,MAX_SOURCES);
        String prompt=buildPrompt(incident,matches);
        String answer= llmClient.complete(prompt);
        List<SuggestionSource>sources=new ArrayList<>();
        for(int i=0;i<matches.size();i++){
            KnowledgeMatch m=matches.get(i);
            sources.add(new SuggestionSource(i+1,m.sourceType(),m.title()));
        }
        return new SuggestionResponse(answer,sources);
    }
    String buildPrompt(IncidentDetail incident,List<KnowledgeMatch>matches){
        StringBuilder p=new StringBuilder();
        p.append("You are an incident assistant. Use ONLY the sources below.\n");
        p.append("Cite sources like [1], [2]. If the sources don't help, say so.\n\n");
        p.append("INCIDENT\n");
        p.append("Service: ").append(incident.service()).append("\n");
        p.append("Title: ").append(incident.title()).append("\n");
        p.append("Severity: ").append(incident.severity()).append("\n\n");
        p.append("SOURCES\n");
        for(int i=0;i<matches.size();i++){
            String content=matches.get(i).content().replace("\n", " ");
            p.append("[").append(i+1).append("] ").append(content).append("\n");
        }
        p.append("\nQUESTION\n");
        p.append("What is the likely cause and the recommended fix? Cite your sources.\n");
        return p.toString();
    }



}
