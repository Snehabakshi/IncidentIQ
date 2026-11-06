package com.incidentiq.backend.repository;

import com.incidentiq.backend.ai.KnowledgeMatch;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class KnowledgeRepository {
    private final JdbcTemplate jdbcTemplate;
    public KnowledgeRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }
    public void upsert(UUID tenantId,String sourceType,UUID sourceId,String title,String content,float[]embedding){
        jdbcTemplate.update("""
                INSERT INTO knowledge_items (tenant_id, source_type, source_id,title,content,embedding)
                VALUES (?,?,?,?,?,?::vector)
                ON CONFLICT (tenant_id, source_type , source_id) WHERE source_id IS NOT NULL
                DO UPDATE SET title=EXCLUDED.title,
                              content=EXCLUDED.CONTENT,
                              embedding=EXCLUDED.embedding
                """,tenantId,sourceType,sourceId,title,content,toVectorLiteral(embedding));
    }
    public List<KnowledgeMatch> search(UUID tenantId,float[]queryEmbedding,int limit){
        String vector=toVectorLiteral(queryEmbedding);
        return jdbcTemplate.query("""
                SELECT id,source_type,source_id,title,content,embedding <=> ?::vector AS distance
                FROM knowledge_items
                WHERE tenant_id= ?
                ORDER BY embedding <=> ?::vector
                LIMIT ?
                """,
                (rs,rowNum)->new KnowledgeMatch(
                        rs.getObject("id", UUID.class),
                        rs.getString("source_type"),
                        rs.getObject("source_id", UUID.class),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getDouble("distance")
                ),vector, tenantId, vector, limit);
    }
    private String toVectorLiteral(float []v){
        StringBuilder sb=new StringBuilder("[");
        for(int i=0;i<v.length;i++){
            if(i>0){
                sb.append(',');
            }
            sb.append(v[i]);
        }
        return sb.append(']').toString();
    }
    public int countByType(UUID tenantId,String sourceType){
        Integer count=jdbcTemplate.queryForObject(
                "SELECT count(*) FROM knowledge_items WHERE tenant_id= ? AND source_type = ?",
                Integer.class,tenantId,sourceType
        );
        return count == null ? 0: count;
    }

}
