package dev.baskakov.eventmanagerservice.kafka.configuration;

import dev.baskakov.eventmanagerservice.kafka.EventNotificationMessage;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfiguration {

    @Bean
    public KafkaTemplate<String, EventNotificationMessage> kafkaTemplate(
            KafkaProperties properties
    ) {
        var props = properties.buildProducerProperties();
        ProducerFactory<String, EventNotificationMessage> factory =
                new DefaultKafkaProducerFactory<>(props);
        return new KafkaTemplate<>(factory);
    }
}
