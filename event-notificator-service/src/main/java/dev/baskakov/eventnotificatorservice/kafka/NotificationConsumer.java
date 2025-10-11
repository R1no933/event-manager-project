package dev.baskakov.eventnotificatorservice.kafka;

import dev.baskakov.eventnotificatorservice.model.NotificationType;
import dev.baskakov.eventnotificatorservice.model.entity.NotificationEntity;
import dev.baskakov.eventnotificatorservice.repository.NotificationRepository;
import dev.baskakov.eventnotificatorservice.utils.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NotificationConsumer {
    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);
    private final NotificationRepository notificationRepository;
    private final Converter converter;

    public NotificationConsumer(
            NotificationRepository notificationRepository,
            Converter converter
    ) {
        this.notificationRepository = notificationRepository;
        this.converter = converter;
    }

    @KafkaListener(topics = "event-notification-topic", groupId = "notification-group")
    @Transactional
    public void consumeNotification(EventNotificationMessage message) {
        log.info("Received notification message: {}", message);

        try {
            String fieldChanges = converter.convertFieldChangesToJson(message);
            NotificationType notificationType = getNotificationType(message);

            for (Long userId : message.users()) {
                var entity = new NotificationEntity(
                        userId,
                        message.eventId(),
                        message.changedByUserId(),
                        notificationType,
                        fieldChanges
                );

                notificationRepository.save(entity);
                log.info("Saved notification entity: {} for user {}", entity,  userId);
            }
            log.info("Saved notification for event {}, and users {} ", message.eventId(), message.users().size());
        } catch (Exception e) {
            log.error("Error while sending notification message {}", message, e);
        }
    }

    private NotificationType getNotificationType(EventNotificationMessage message) {
        if (message.status() != null) {
            return NotificationType.UPDATED_STATUS;
        }

        boolean hasChanges = message.name() != null ||
                message.maxPlaces() != null ||
                message.date() != null ||
                message.cost() != null ||
                message.duration() != null ||
                message.locationId() != null;

        return hasChanges ? NotificationType.UPDATED_EVENT : NotificationType.UPDATED_STATUS;
    }
}
