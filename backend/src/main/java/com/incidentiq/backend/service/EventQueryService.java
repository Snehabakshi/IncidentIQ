package com.incidentiq.backend.service;

import com.incidentiq.backend.dto.EventSummary;
import com.incidentiq.backend.dto.PageResponse;
import com.incidentiq.backend.entity.Event;
import com.incidentiq.backend.repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EventQueryService {
    private static final int DEFAULT_SIZE=50;
    private static final int MAX_SIZE=100 ;
    private final EventRepository eventRepository;
    public EventQueryService(EventRepository eventRepository){
        this.eventRepository=eventRepository;
    }
    @Transactional(readOnly = true)
    public PageResponse<EventSummary> list(UUID tenantId,String service,int page,int size){
        int safePage=Math.max(page,0);
        int safeSize=size<1 ? DEFAULT_SIZE: Math.min(size,MAX_SIZE);
        Pageable pageable= PageRequest.of(safePage,safeSize, Sort.by(Sort.Direction.DESC,"receivedAt") .and(Sort.by(Sort.Direction.DESC,"id")));
        String serviceFilter=(service==null || service.isBlank())?null :service.trim();
        Page<Event> result=(serviceFilter==null)?eventRepository.findByTenantId(tenantId,pageable):eventRepository.findByTenantIdAndService(tenantId,serviceFilter,pageable);
        return new PageResponse<>(
                result.getContent().stream().map(EventSummary::from).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }
}
