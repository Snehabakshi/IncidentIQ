package com.incidentiq.backend.repository;

import com.incidentiq.backend.dto.StoredSuggestion;
import jdk.jshell.SourceCodeAnalysis;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class SuggestionRepository {
    private final JdbcTemplate jdbcTemplate;
    public SuggestionRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }
    public UUID insert(UUID tenantId,UUID incidentId,String answer,String sourceJson){
        return jdbcTemplate.queryForObject("""
                INSERT INTO suggestions (tenant_id,incident_id,answer,sources)
                VALUES (?,?,?,?::jsonb)
                RETURNING id
                """, UUID.class,tenantId,incidentId,answer,sourceJson);
    }
    public Optional<StoredSuggestion>findLatest(UUID tenantId,UUID incidentId){
        return jdbcTemplate.query("""
                SELECT id,answer,sources
                FROM suggestions
                WHERE tenant_id=? AND incident_id=?
                ORDER BY created_at DESC
                LIMIT 1
                """,
                (rs,rowNum) -> new StoredSuggestion(
                        rs.getObject("id",UUID.class),
                        rs.getString("answer"),
                        rs.getString("sources")
                ),tenantId,incidentId).stream().findFirst();
    }
    public boolean existsForTenant(UUID tenantId, UUID suggestionId){
        Integer count=jdbcTemplate.queryForObject(
                "SELECT count(*) FROM suggestions WHERE id=? AND tenant_id=?",
                Integer.class,suggestionId,tenantId
        );
        return count!=null && count>0;
    }
}
