package dev.baskakov.eventmanagerservice.events.event;

import dev.baskakov.eventmanagerservice.events.registration.EventRegistration;

import java.time.LocalDateTime;
import java.util.List;

public record Event(
        Long id,
        String name,
        Long ownerId,
        Integer maxPlaces,
        List<EventRegistration> registrationList,
        LocalDateTime date,
        Integer cost,
        Integer duration,
        Long locationId,
        EventStatus status
) {
}
