package com.incidentiq.backend.service;

import com.incidentiq.backend.entity.Event;
import com.incidentiq.backend.entity.Incident;
import com.incidentiq.backend.entity.IncidentStatus;
import com.incidentiq.backend.entity.Severity;
import com.incidentiq.backend.grouping.SignatureUtils;
import com.incidentiq.backend.messaging.EventMessage;
import com.incidentiq.backend.repository.EventRepository;
import com.incidentiq.backend.repository.IncidentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class EventStorageService {
    private static final Logger log=LoggerFactory.getLogger(EventStorageService.class);
    private static final int MAX_ATTEMPTS=2;
    private final EventRepository eventRepository;
    private final IncidentRepository incidentRepository;
    private final TransactionTemplate transactionTemplate;

    public EventStorageService(EventRepository eventRepository, IncidentRepository incidentRepository, PlatformTransactionManager transactionManager){
        this.eventRepository=eventRepository;
        this.incidentRepository=incidentRepository;
        this.transactionTemplate=new TransactionTemplate(transactionManager);
    }
    public void store(EventMessage m){
        if(eventRepository.existsById(m.eventId())){
            log.info("Event already stored, skipping redelivery: eventId={}",m.eventId());
            return;
        }
        for(int attempt=1;attempt<=MAX_ATTEMPTS;attempt++){
            try{
                transactionTemplate.executeWithoutResult(status ->saveEventAndIncident(m));
                return;
            }
            catch(DataIntegrityViolationException e){
                if(isDuplicateExternalId(m)){
                    log.info("Duplicate event skip  ped: tenant={}, service={}, externalId={}",
                            m.tenantId(), m.service(), m.externalId());
                    return;
                }
                if(attempt==MAX_ATTEMPTS){
                    throw e;
                }
                log.info("Incident race detected, retrying: eventId={}", m.eventId());
            }
        }
    }
    private void saveEventAndIncident(EventMessage m){
        Event event=new Event(m.eventId(),m.tenantId(),m.apiKeyId(),m.service(),m.severity(),m.message(),m.occurredAt(),m.externalId(),m.receivedAt());
        if(m.severity() == Severity.ERROR || m.severity()==Severity.CRITICAL){
            String signature= SignatureUtils.signature(m.message());
            Incident incident=findOrCreateIncident(m,signature);
            event.assignToIncident(signature,incident.getId());
            eventRepository.save(event);
            incidentRepository.recordEvent(incident.getId(),m.receivedAt());
        }
        else{
            eventRepository.save(event);
        }

    }
    private Incident findOrCreateIncident(EventMessage m, String signature){
        return incidentRepository
                .findByTenantIdAndServiceAndSignatureAndStatus(m.tenantId(),m.service(),signature, IncidentStatus.OPEN)
                .orElseGet(()->incidentRepository.saveAndFlush(
                        new Incident(m.tenantId(),m.service(),signature,m.message(),m.severity(),m.receivedAt())
                ));
    }
    private boolean isDuplicateExternalId(EventMessage m){
        return m.externalId()!=null
                && eventRepository.findByTenantIdAndServiceAndExternalId(
                m.tenantId(), m.service(), m.externalId()).isPresent();
    }


}
