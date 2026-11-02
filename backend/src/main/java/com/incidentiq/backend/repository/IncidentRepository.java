package com.incidentiq.backend.repository;

import com.incidentiq.backend.entity.IncidentStatus;
import com.incidentiq.backend.entity.Incident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface IncidentRepository extends JpaRepository<Incident, UUID> {
    Optional<Incident> findByTenantIdAndServiceAndSignatureAndStatus(UUID tenantId, String service, String signature, IncidentStatus status);
    Page<Incident> findByTenantId(UUID tenantId, Pageable pageable);
    Page<Incident> findByTenantIdAndStatus(UUID tenantId,IncidentStatus status,Pageable pageable);
    @Modifying
    @Query("""
        UPDATE Incident i
        SET i.eventCount = i.eventCount + 1,
            i.lastSeenAt = CASE WHEN i.lastSeenAt < :seenAt THEN :seenAt ELSE i.lastSeenAt END
        WHERE i.id = :id
        """)
    int recordEvent(@Param("id") UUID id,@Param("seenAt") Instant seenAt);

}
