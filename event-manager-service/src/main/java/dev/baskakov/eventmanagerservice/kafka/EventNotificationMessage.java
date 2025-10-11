package dev.baskakov.eventmanagerservice.kafka;

import dev.baskakov.eventmanagerservice.events.event.model.EventStatus;

import java.time.LocalDateTime;
import java.util.List;

public record EventNotificationMessage(
        Long eventId,
        List<Long> users,
        Long ownerId,
        Long changedByUserId,
        FieldChange<String> name,
        FieldChange<Integer> maxPlaces,
        FieldChange<LocalDateTime> date,
        FieldChange<Integer> cost,
        FieldChange<Integer> duration,
        FieldChange<Long> locationId,
        FieldChange<EventStatus> status
) {
    public EventNotificationMessage {
        if (users == null) {
            users = List.of();
        }
    }
}
