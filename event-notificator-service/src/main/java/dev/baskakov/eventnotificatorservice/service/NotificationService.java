package dev.baskakov.eventnotificatorservice.service;

import dev.baskakov.eventnotificatorservice.model.dto.MarkAsReadRequestDTO;
import dev.baskakov.eventnotificatorservice.model.dto.NotificationResponseDTO;
import dev.baskakov.eventnotificatorservice.model.entity.NotificationEntity;
import dev.baskakov.eventnotificatorservice.repository.NotificationRepository;
import dev.baskakov.eventnotificatorservice.utils.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository notificationRepository;
    private final Converter converter;

    public NotificationService(
            NotificationRepository notificationRepository,
            Converter converter
    ) {
        this.notificationRepository = notificationRepository;
        this.converter = converter;
    }

    public List<NotificationResponseDTO> getNotificationsByUserId(Long userId) {
        log.info("Getting notifications by userId {}", userId);

        var listEntity = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        var listDTO = listEntity
                .stream()
                .map(converter::toResponseDTO)
                .toList();

        return listDTO;
    }

    @Transactional
    public void makeNotificationAsRead(MarkAsReadRequestDTO requestDTO) {
        List<Long> notificationIds = requestDTO.notificationIds();

        if (notificationIds.isEmpty()) {
            log.info("No notifications for making as read");
            return;
        }

        log.info("Making {} notifications for marked as read", notificationIds.size());

        List<NotificationEntity> existingNotifications =
                notificationRepository.findByIdIn(notificationIds);

        List<Long> existingIds =
                existingNotifications
                        .stream()
                        .map(NotificationEntity::getId)
                        .toList();

        if (!existingIds.isEmpty()) {
            notificationRepository.makeAsRead(existingIds);
            log.info("Successfully made {} notifications for marked as read", existingIds.size());
        }

        List<Long> notFoundIds = notificationIds
                .stream()
                .filter(id -> !existingIds.contains(id))
                .toList();
        if (!notFoundIds.isEmpty()) {
            log.info("Not found {} notifications for marked as read", notFoundIds);
        }
    }
}
