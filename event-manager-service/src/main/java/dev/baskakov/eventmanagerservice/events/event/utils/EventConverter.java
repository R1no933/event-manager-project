package dev.baskakov.eventmanagerservice.events.event.utils;

import dev.baskakov.eventmanagerservice.events.event.Event;
import dev.baskakov.eventmanagerservice.events.event.EventDto;
import dev.baskakov.eventmanagerservice.events.event.EventEntity;
import dev.baskakov.eventmanagerservice.events.registration.EventRegistration;
import dev.baskakov.eventmanagerservice.events.registration.EventRegistrationEntity;
import org.springframework.stereotype.Component;

@Component
public class EventConverter {
    public Event toDomainFromEntity(EventEntity eventEntity) {
        return new Event(
                eventEntity.getId(),
                eventEntity.getName(),
                eventEntity.getOwnerId(),
                eventEntity.getMaxPlaces(),
                eventEntity.getRegistrationList()
                        .stream()
                        .map(ere -> new EventRegistration(
                                ere.getId(),
                                ere.getUserId(),
                                eventEntity.getId())
                        ).toList(),
                eventEntity.getDate(),
                eventEntity.getCost(),
                eventEntity.getDuration(),
                eventEntity.getLocationId(),
                eventEntity.getStatus()

        );
    }

    public EventDto toDtoFromDomain(Event event) {
        return new EventDto(
                event.id(),
                event.name(),
                event.ownerId(),
                event.maxPlaces(),
                event.registrationList().size(),
                event.date(),
                event.cost(),
                event.duration(),
                event.locationId(),
                event.status()
        );
    }
}
