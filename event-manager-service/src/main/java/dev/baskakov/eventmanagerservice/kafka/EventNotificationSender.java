package dev.baskakov.eventmanagerservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventNotificationSender {

    private static final Logger log = LoggerFactory.getLogger(EventNotificationSender.class);
    private final KafkaTemplate<String, EventNotificationMessage> kafkaTemplate;

    public EventNotificationSender(KafkaTemplate<String, EventNotificationMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendNotification(EventNotificationMessage eventNotificationMessage) {
        log.info("Send event notification message to kafka {}", eventNotificationMessage);
        String key = "event-" + eventNotificationMessage.eventId();
        String topic = "event-notification-topic";
        kafkaTemplate.send(topic, key, eventNotificationMessage);
    }
}
