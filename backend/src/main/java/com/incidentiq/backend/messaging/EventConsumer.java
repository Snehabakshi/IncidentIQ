package com.incidentiq.backend.messaging;

import tools.jackson.databind.json.JsonMapper;
import com.incidentiq.backend.service.EventStorageService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {
    private final JsonMapper jsonMapper;
    private  final EventStorageService storageService;
    public EventConsumer(JsonMapper jsonMapper,EventStorageService storageService){
        this.jsonMapper=jsonMapper;
        this.storageService=storageService;
    }
    @KafkaListener(topics="${app.kafka.topic.raw-events}")
    public void onMessage(String json){
        EventMessage message=jsonMapper.readValue(json, EventMessage.class);
        storageService.store(message);

    }

}
