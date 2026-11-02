package com.incidentiq.backend.service;

import com.incidentiq.backend.dto.IncidentSummary;
import com.incidentiq.backend.dto.PageResponse;
import com.incidentiq.backend.entity.Incident;
import com.incidentiq.backend.entity.IncidentStatus;
import com.incidentiq.backend.repository.IncidentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class IncidentQueryService {
    private static final int DEFAULT_SIZE=50;
    private static final int MAX_SIZE=100;
    private final IncidentRepository incidentRepository;

    public IncidentQueryService(IncidentRepository incidentRepository){
        this.incidentRepository=incidentRepository;
    }
    @Transactional(readOnly=true)
    public PageResponse<IncidentSummary>list(UUID tenantId, IncidentStatus status,int page,int size){
        int safePage=Math.max(page,0);
        int safeSize=size<1 ?DEFAULT_SIZE: Math.min(size,MAX_SIZE);
        Pageable pageable= PageRequest.of(safePage,safeSize, Sort.by(Sort.Direction.DESC,"lastSeenAt").and(Sort.by(Sort.Direction.DESC, "id")));
        Page<Incident> result=(status==null )?incidentRepository.findByTenantId(tenantId,pageable):incidentRepository.findByTenantIdAndStatus(tenantId,status,pageable);
        return new PageResponse<>(
                result.getContent().stream().map(IncidentSummary::from).toList(),
                result.getNumber(),result.getSize(),
                result.getTotalElements(), result.getTotalPages()
        );
    }
}
