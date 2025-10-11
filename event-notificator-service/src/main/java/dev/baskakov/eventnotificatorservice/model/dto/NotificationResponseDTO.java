package dev.baskakov.eventnotificatorservice.model.dto;

import java.util.Map;

public record NotificationResponseDTO(
        Long eventId,
        Map<String, Object> fieldChanges
) {
}
