package com.incidentiq.backend.service;

import com.incidentiq.backend.ai.KnowledgeMatch;
import com.incidentiq.backend.dto.EventSummary;
import com.incidentiq.backend.dto.IncidentDetail;
import com.incidentiq.backend.dto.IncidentSummary;
import com.incidentiq.backend.dto.PageResponse;
import com.incidentiq.backend.entity.Event;
import com.incidentiq.backend.entity.Incident;
import com.incidentiq.backend.entity.IncidentStatus;
import com.incidentiq.backend.exception.IncidentNotFoundException;
import com.incidentiq.backend.repository.EventRepository;
import com.incidentiq.backend.repository.IncidentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class IncidentQueryService {
    private static final int DEFAULT_SIZE=50;
    private static final int MAX_SIZE=100;
    private final IncidentRepository incidentRepository;
    private final EventRepository eventRepository;
    private final KnowledgeService knowledgeService;
    public IncidentQueryService(IncidentRepository incidentRepository, EventRepository eventRepository, KnowledgeService knowledgeService){
        this.incidentRepository=incidentRepository;
        this.eventRepository=eventRepository;
        this.knowledgeService=knowledgeService;
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
    @Transactional(readOnly = true)
    public IncidentDetail get(UUID tenantId,UUID incidentId){
        Incident incident=incidentRepository.findByIdAndTenantId(incidentId,tenantId).orElseThrow(() -> new IncidentNotFoundException(incidentId));
        return IncidentDetail.from(incident);
    }
    @Transactional(readOnly = true)
    public PageResponse<EventSummary> listEvents(UUID tenantId, UUID incidentId, int page, int size) {
        incidentRepository.findByIdAndTenantId(incidentId, tenantId)
                .orElseThrow(() -> new IncidentNotFoundException(incidentId));
        int safePage = Math.max(page, 0);
        int safeSize = size < 1 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        Pageable pageable = PageRequest.of(safePage, safeSize,
                Sort.by(Sort.Direction.DESC, "receivedAt").and(Sort.by(Sort.Direction.DESC, "id")));
        Page<Event> result = eventRepository.findByTenantIdAndIncidentId(tenantId, incidentId, pageable);
        return new PageResponse<>(
                result.getContent().stream().map(EventSummary::from).toList(),
                result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }
    @Transactional(readOnly = true)
    public List<KnowledgeMatch> similar(UUID tenantId,UUID incidentId,int limit){
        Incident incident=incidentRepository.findByIdAndTenantId(incidentId,tenantId).orElseThrow(()-> new IncidentNotFoundException(incidentId));
        String text=incident.getService()+ ": " +incident.getTitle();
        int safeLimit=Math.max(1,Math.min(limit,10));
        return knowledgeService.findSimilar(tenantId,text,safeLimit).stream()
                .filter(m -> !incidentId.equals(m.sourceId()))
                .limit(safeLimit).toList();

    }
}
