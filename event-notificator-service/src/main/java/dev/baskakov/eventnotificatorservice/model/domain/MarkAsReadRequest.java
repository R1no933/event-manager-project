package dev.baskakov.eventnotificatorservice.model.domain;

import java.util.List;

public record MarkAsReadRequest(
        List<Long> notificationIds
) {
}
