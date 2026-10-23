package com.incidentiq.backend.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic rawEventsDeadLetterTopic(@Value("${app.kafka.topic.raw-events}") String topic){
        return TopicBuilder.name(topic + "-dlt").partitions(3).replicas(1).build();
    }
    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String,String>kafkaTemplate){
        DeadLetterPublishingRecoverer recoverer=new DeadLetterPublishingRecoverer(kafkaTemplate);
        return new DefaultErrorHandler(recoverer,new FixedBackOff(1000L,3L));
    }
}
