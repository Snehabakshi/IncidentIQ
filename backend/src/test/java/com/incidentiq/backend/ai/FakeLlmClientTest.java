package com.incidentiq.backend.ai;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FakeLlmClientTest {
    private final FakeLlmClient client=new FakeLlmClient();
    @Test
    void usesFirstSourceAsCauseAndListAllActions(){
        String prompt= """
                You are an incident assistant. Use ONLY the sources below.
                SOURCES
                [1] payment-api: DB connection timeout after 3279ms on host db-3. Resolution: Increased the pool size and restarted the service
                [2] Database connection pool exhausted. Symptoms: pool exhausted. Steps: check the maximum pool size, look for slow queries
                [3] Slow responses and high latency. Symptoms: requests timing out. Steps: check CPU and memory
                QUESTION
                What is the likely cause and fix?
                """;
        String answer=client.complete(prompt);
        assertTrue(answer.contains("payment-api: DB connection timeout after 3279ms on host db-3 [1]"));
        assertTrue(answer.contains("1. Increased the pool size and restarted the service [1]"));
        assertTrue(answer.contains("2. check the maximum pool size, look for slow queries [2]"));
        assertTrue(answer.contains("3. check CPU and memory [3]"));
        assertTrue(answer.contains("Confidence: medium (based on 3 source(s))."));


    }
    @Test
    void singleRunbookGivesLowConfidence(){
        String prompt= """
                SOURCES
                [1] Database connection pool exhausted. Symptoms: pool exhausted. Steps: check the maximum pool size, restart the service
                QUESTION
                What is the likely cause and fix?
                """;
        String answer=client.complete(prompt);
        assertTrue(answer.contains("Database connection pool exhausted [1]"));
        assertTrue(answer.contains("1. check the maximum pool size, restart the service [1]"));
        assertTrue(answer.contains("Confidence: low (based on 1 source(s))."));

    }
    @Test
    void noSourcesivesNoSuggestion(){
        String prompt= """
                SOURCES
                QUESTION
                What is the likely cause and fix?
                """;
        assertEquals("No relevant sources found; I can't suggest a fix.", client.complete(prompt));

    }
}
