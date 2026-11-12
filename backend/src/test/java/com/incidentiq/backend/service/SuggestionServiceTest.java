package com.incidentiq.backend.service;

import com.incidentiq.backend.ai.FakeLlmClient;
import com.incidentiq.backend.ai.KnowledgeMatch;
import com.incidentiq.backend.dto.IncidentDetail;
import com.incidentiq.backend.entity.IncidentStatus;
import com.incidentiq.backend.entity.Severity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class SuggestionServiceTest {
    private final SuggestionService service=new SuggestionService(null,new FakeLlmClient());
    private IncidentDetail incident(){
        return new IncidentDetail(UUID.randomUUID(), "checkout-api","DB connection timeout after <num>ms", Severity.ERROR, IncidentStatus.OPEN,10, Instant.now(),Instant.now(),null,null,null);
    }
    private KnowledgeMatch match(String type,String title,String content){
        return new KnowledgeMatch(UUID.randomUUID(),type,UUID.randomUUID(),title,content,0.3);
    }
    @Test
    void promptContainsIncidentAndNumberedSources() {
        List<KnowledgeMatch>matches= List.of(
                match("INCIDENT","payment-api timeout","payment-api timeout. Resolution: Increased the pool size"),
                match("RUNBOOK","Pool exhausted","Pool exhausted. Steps: check the maximum pool size"));
        String prompt=service.buildPrompt(incident(),matches);
        assertTrue(prompt.contains("Service: checkout-api"));
        assertTrue(prompt.contains("Severity: ERROR"));
        assertTrue(prompt.contains("[1] payment-api timeout. Resolution: Increased the pool size"));
        assertTrue(prompt.contains("[2] Pool exhausted. Steps: check the maximum pool size"));
        assertTrue(prompt.contains("Use ONLY the sources below"));

    }
    @Test
    void lineBreaksInsideSourcesAreFlattened(){
        List<KnowledgeMatch>matches=List.of(
                match("INCIDENT","t","Resolution: first line\nsecond line"));
        String prompt=service.buildPrompt(incident(),matches);
        assertTrue(prompt.contains("[1] Resolution: first line second line"));
    }
    @Test
    void noMatchesGivesNoSourceLines(){
        String prompt=service.buildPrompt(incident(),List.of());
        assertFalse(prompt.contains("\n[1] "));
    }
    @Test
    void promptWorksWithTheFakeLlmClient(){
        List<KnowledgeMatch>matches=List.of(
                match("INCIDENT", "payment-api timeout", "payment-api timeout. Resolution: Increased the pool size"));
        String answer=new FakeLlmClient().complete(service.buildPrompt(incident(),matches));
        assertTrue(answer.contains("Increased the pool size [1]"));

    }

}
