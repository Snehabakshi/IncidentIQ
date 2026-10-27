package com.incidentiq.backend.repository;

import com.incidentiq.backend.entity.IncidentStatus;
import com.incidentiq.backend.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IncidentRepository extends JpaRepository<Incident, UUID> {
    Optional<Incident> findByTenantIdAndServiceAndSignatureAndStatus(UUID tenantId, String service, String signature, IncidentStatus status);

}
