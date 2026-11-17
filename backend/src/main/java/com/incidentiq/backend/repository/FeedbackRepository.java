package com.incidentiq.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class FeedbackRepository {
    private final JdbcTemplate jdbcTemplate;
    public FeedbackRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }
    public void upsert(UUID tenantId,UUID suggestionId,UUID userId,String rating,String comment){
        jdbcTemplate.update("""
                INSERT INTO suggestion_feedback (tenant_id,suggestion_id,user_id,rating,comment)
                VALUES(?,?,?,?)
                ON CONFLICT(suggestion_id,user_id)
                DO UPDATE SET rating=EXCLUDED.rating,
                              comment=EXCLUDED.comment,
                              updated_at=now()
                """,tenantId,suggestionId,userId,rating,comment);
    }
}
