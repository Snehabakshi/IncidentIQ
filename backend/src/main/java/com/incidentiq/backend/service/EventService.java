package com.incidentiq.backend.service;

import com.incidentiq.backend.dto.CreateEventRequest;
import com.incidentiq.backend.dto.EventResponse;
import com.incidentiq.backend.entity.Event;
import com.incidentiq.backend.messaging.EventMessage;
import com.incidentiq.backend.messaging.EventProducer;
import com.incidentiq.backend.repository.EventRepository;
import com.incidentiq.backend.security.ApiKeyPrincipal;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventService {
    private final EventProducer eventProducer;
    public EventService(EventProducer  eventProducer){
        this.eventProducer=eventProducer;
    }
    public EventResponse accept(ApiKeyPrincipal principal,CreateEventRequest request){
        Instant now=Instant.now();
        EventMessage message=new EventMessage(UUID.randomUUID(),principal.tenantId(),principal.apiKeyId(),request.service().trim(),request.severity(),request.message(),request.occurredAt()!=null ? request.occurredAt():now,now,normalize(request.externalId()));
        eventProducer.publish(message);
        return new EventResponse(message.eventId(),"ACCEPTED",now);
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
