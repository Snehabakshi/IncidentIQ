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
    public IncidentService(IncidentRepository incidentRepository){
        this.incidentRepository=incidentRepository;
    }
    @Transactional
    public IncidentDetail resolve(UUID tenantId,UUID userId,UUID incidentId,String note){
        Incident incident =incidentRepository.findByIdAndTenantId(incidentId,tenantId).orElseThrow(()->new IncidentNotFoundException(incidentId));
        incident.resolve(userId,note.trim());
        return IncidentDetail.from(incident);
    }
}
