package com.incidentiq.backend.repository;

import com.incidentiq.backend.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
    boolean existsByTenantIdAndServiceAndExternalId(UUID tenantId, String service, String externalId);
    List<Event> findTop50ByTenantIdOrderByReceivedAtDesc(UUID tenantId);
    Optional<Event>findByTenantIdAndServiceAndExternalId(UUID tenantId,String service,String externalId);
    Page<Event> findByTenantId(UUID tenantId, Pageable pageable);
    Page<Event> findByTenantIdAndService(UUID tenantId,String service,Pageable pageable);
}
