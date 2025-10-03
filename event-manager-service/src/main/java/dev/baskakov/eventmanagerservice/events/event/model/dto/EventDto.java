package dev.baskakov.eventmanagerservice.events.event.model.dto;

import dev.baskakov.eventmanagerservice.events.event.model.EventStatus;

import java.time.LocalDateTime;

public record EventDto(
        Long id,
        String name,
        Long ownerId,
        Integer maxPlaces,
        Integer occupiedPlace,
        LocalDateTime date,
        Integer cost,
        Integer duration,
        Long locationId,
        EventStatus status
) {
}
