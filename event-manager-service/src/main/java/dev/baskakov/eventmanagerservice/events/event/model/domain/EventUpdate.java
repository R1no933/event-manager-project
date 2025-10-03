package dev.baskakov.eventmanagerservice.events.event.model.domain;

import java.time.LocalDateTime;

public record EventUpdate(
        String name,
        Integer maxPlaces,
        LocalDateTime date,
        Integer cost,
        Integer duration,
        Long locationId
) {
}
