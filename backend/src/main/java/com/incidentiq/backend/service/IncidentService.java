package com.incidentiq.backend.service;

import com.incidentiq.backend.dto.IncidentDetail;
import com.incidentiq.backend.entity.Incident;
import com.incidentiq.backend.entity.IncidentStatus;
import com.incidentiq.backend.exception.IncidentNotFoundException;
import com.incidentiq.backend.repository.IncidentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class IncidentService {
    private final IncidentRepository incidentRepository;
    private final knowledgeService knowledgeService;
    public IncidentService(IncidentRepository incidentRepository, knowledgeService knowledgeService){
        this.incidentRepository=incidentRepository;
        this.knowledgeService=knowledgeService;
    }
    @Transactional
    public IncidentDetail resolve(UUID tenantId,UUID userId,UUID incidentId,String note){
        Incident incident =incidentRepository.findByIdAndTenantId(incidentId,tenantId).orElseThrow(()->new IncidentNotFoundException(incidentId));
        incident.resolve(userId,note.trim());
        knowledgeService.indexResolvedToIncident(incident);
        return IncidentDetail.from(incident);
    }
}
