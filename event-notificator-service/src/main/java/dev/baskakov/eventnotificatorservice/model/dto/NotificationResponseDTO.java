package dev.baskakov.eventnotificatorservice.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.baskakov.eventnotificatorservice.model.NotificationType;

import java.time.LocalDateTime;
import java.util.Map;

public record NotificationResponseDTO(
        Long id,
        Long userId,
        Long eventId,
        Long changedByUserId,
        NotificationType notificationType,
        Map<String, Object> fieldChanges,
        Boolean isRead,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime readAt
) {
}
