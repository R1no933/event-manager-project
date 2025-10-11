package dev.baskakov.eventnotificatorservice.model.domain;

import java.util.Map;

public record NotificationResponse(
        Long eventId,
        Map<String, Object> fieldChanges
) {
}
