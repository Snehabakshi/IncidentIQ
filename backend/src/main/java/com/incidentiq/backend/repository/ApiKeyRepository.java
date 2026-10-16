package com.incidentiq.backend.repository;

import com.incidentiq.backend.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    Optional<ApiKey> findByKeyHash(String keyHash);
    Optional<ApiKey> findByIdAndTenantId(UUID id, UUID tenantId);
    List<ApiKey> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);
    @Modifying
    @Transactional
    @Query("""
        UPDATE ApiKey k
        SET k.lastUsedAt= :now
        where k.id= :id
        AND (k.lastUsedAt IS NULL OR k.lastUsedAt< :threshold)
        """)
    int touch(@Param("id") UUID id,
              @Param("now") Instant now,
              @Param("threshold") Instant threshold);
}

