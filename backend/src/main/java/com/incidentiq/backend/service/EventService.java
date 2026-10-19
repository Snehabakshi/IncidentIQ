package com.incidentiq.backend.service;

import com.incidentiq.backend.dto.CreateEventRequest;
import com.incidentiq.backend.dto.EventResponse;
import com.incidentiq.backend.entity.Event;
import com.incidentiq.backend.repository.EventRepository;
import com.incidentiq.backend.security.ApiKeyPrincipal;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventService {
    private final EventRepository eventRepository;
    public EventService(EventRepository eventRepository){
        this.eventRepository=eventRepository;
    }
    public EventResponse ingest(ApiKeyPrincipal principal , CreateEventRequest request){
        UUID tenantId=principal.tenantId();
        String service=request.service().trim();
        String externalId=normalize(request.externalId());
        Instant occurredAt=request.occurredAt()!=null ?request.occurredAt(): Instant.now();
        if(externalId!=null){
            Optional<Event> existing=eventRepository.findByTenantIdAndServiceAndExternalId(tenantId,service,externalId);
            if(existing.isPresent()){
                return duplicate(existing.get());
            }
        }
        Event event=new Event(tenantId,principal.apiKeyId(),service,request.severity(), request.message(), occurredAt,externalId);
        try{
            Event saved=eventRepository.save(event);
            return new EventResponse(saved.getId(),"CREATED",saved.getReceivedAt());

        }
        catch(DataIntegrityViolationException e){
            if(externalId==null){
                throw e;
            }
            return eventRepository.findByTenantIdAndServiceAndExternalId(tenantId,service,externalId).map(this::duplicate).orElseThrow(()->e);
        }

    }
    private EventResponse duplicate(Event existing){
        return new EventResponse(existing.getId(),"DUPLICATE",existing.getReceivedAt());

    }
    private String normalize(String value){
        if(value==null){
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

}
