package com.incidentiq.backend.messaging;

import tools.jackson.databind.json.JsonMapper;
import com.incidentiq.backend.exception.EventPublishException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class EventProducer {
    private final KafkaTemplate<String,String>kafkaTemplate;
    private final JsonMapper jsonMapper;
    private final String topic;
    public EventProducer(KafkaTemplate<String, String> kafkaTemplate, JsonMapper jsonMapper, @Value("${app.kafka.topic.raw-events}") String topic){
        this.kafkaTemplate=kafkaTemplate;
        this.jsonMapper=jsonMapper;
        this.topic=topic;
    }
    public void publish(EventMessage message)  {
        String key= message.tenantId() +":"+message.service();
        String json= jsonMapper.writeValueAsString(message);
        try{
            kafkaTemplate.send(topic,key,json).get(5, TimeUnit.SECONDS);
        }
        catch(InterruptedException e){
            Thread.currentThread().interrupt();
            throw new EventPublishException("Interrupted while publishing event", e);
        }
        catch(ExecutionException | TimeoutException e){
            throw new EventPublishException("Kafka did not confirm the event", e);
        }
    }
}
