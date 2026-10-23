package com.incidentiq.backend.service;

import com.incidentiq.backend.entity.Event;
import com.incidentiq.backend.messaging.EventMessage;
import com.incidentiq.backend.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class EventStorageService {
    private static final Logger log= LoggerFactory.getLogger(EventStorageService.class);
    private final EventRepository eventRepository;
    public EventStorageService(EventRepository eventRepository){
        this.eventRepository=eventRepository;
    }
    public void store(EventMessage m){
        Event event=new Event(m.eventId(),m.tenantId(),m.apiKeyId(),m.service(),m.severity(),m.message(),m.occurredAt(),m.externalId(),m.receivedAt());
        try{
            eventRepository.save(event);
        }
        catch(DataIntegrityViolationException e){
            boolean duplicate = m.externalId()!=null && eventRepository.findByTenantIdAndServiceAndExternalId(m.tenantId(),m.service(),m.externalId()).isPresent();
            if(duplicate){
                log.info("Duplicate event skipped: tenant={}, service={}, externalId={}",m.tenantId(),m.service(),m.externalId());
                return;
            }
            throw e;
        }
    }
}
