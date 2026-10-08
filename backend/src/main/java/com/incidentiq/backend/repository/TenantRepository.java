package com.incidentiq.backend.repository;

import com.incidentiq.backend.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    //optional means the result may or may not exist this will force us to handle null handling
    Optional<Tenant> findByName(String name);

    boolean existsByName(String name);
}
