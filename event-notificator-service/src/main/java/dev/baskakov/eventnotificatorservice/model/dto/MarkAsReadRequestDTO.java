package dev.baskakov.eventnotificatorservice.model.dto;

import java.util.List;

public record MarkAsReadRequestDTO(
        List<Long> notificationIds
) {
    public MarkAsReadRequestDTO {
        if (notificationIds == null) {
            notificationIds = List.of();
        }
    }
}
