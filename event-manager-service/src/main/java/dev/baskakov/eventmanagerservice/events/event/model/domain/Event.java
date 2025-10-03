package dev.baskakov.eventmanagerservice.events.event.model.domain;

import dev.baskakov.eventmanagerservice.events.event.model.EventStatus;
import dev.baskakov.eventmanagerservice.events.registration.model.domain.EventRegistration;
import dev.baskakov.eventmanagerservice.location.model.domain.Location;

import javax.swing.event.ListSelectionEvent;
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
