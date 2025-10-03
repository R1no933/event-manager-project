package dev.baskakov.eventmanagerservice.events.event.utils;

import dev.baskakov.eventmanagerservice.events.event.model.EventStatus;
import dev.baskakov.eventmanagerservice.events.event.model.domain.Event;
import dev.baskakov.eventmanagerservice.events.event.model.domain.EventSearch;
import dev.baskakov.eventmanagerservice.events.event.model.domain.EventUpdate;
import dev.baskakov.eventmanagerservice.events.event.model.dto.EventCreateRequestDto;
import dev.baskakov.eventmanagerservice.events.event.model.dto.EventDto;
import dev.baskakov.eventmanagerservice.events.event.model.dto.EventSearchRequestDto;
import dev.baskakov.eventmanagerservice.events.event.model.dto.EventUpdateRequestDto;
import dev.baskakov.eventmanagerservice.events.event.model.entity.EventEntity;
import dev.baskakov.eventmanagerservice.events.registration.model.domain.EventRegistration;
import dev.baskakov.eventmanagerservice.events.registration.model.entity.EventRegistrationEntity;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public Event toDomainFromCreateRequestDto(
            EventCreateRequestDto eventCreateRequestDto,
            Long userId) {
        return new Event(
                null,
                eventCreateRequestDto.name(),
                userId,
                eventCreateRequestDto.maxPlaces(),
                List.of(),
                eventCreateRequestDto.date(),
                eventCreateRequestDto.cost(),
                eventCreateRequestDto.duration(),
                eventCreateRequestDto.locationId(),
                EventStatus.WAIT_START
        );
    }

    public Event applyUpdateRequestDto(
            Event event,
            EventUpdateRequestDto eventUpdateRequestDto
            ) {
        return new Event(
                event.id(),
                eventUpdateRequestDto.name() != null
                        ? eventUpdateRequestDto.name()
                        : event.name(),
                event.ownerId(),
                eventUpdateRequestDto.maxPlaces() != null
                        ? eventUpdateRequestDto.maxPlaces()
                        : event.maxPlaces(),
                event.registrationList(),
                eventUpdateRequestDto.date() != null
                        ? eventUpdateRequestDto.date()
                        : event.date(),
                eventUpdateRequestDto.cost() != null
                        ? eventUpdateRequestDto.cost()
                        : event.cost(),
                eventUpdateRequestDto.duration() != null
                        ? eventUpdateRequestDto.duration()
                        : event.duration(),
                eventUpdateRequestDto.locationId() != null
                        ? eventUpdateRequestDto.locationId()
                        : event.locationId(),
                event.status()
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


    public EventEntity toEntityFromDomain(Event event) {
        return new EventEntity(
                event.id() != null ? event.id() : null,
                event.name(),
                event.ownerId(),
                event.maxPlaces(),
                event.registrationList()
                        .stream()
                        .map(e ->
                                new EventRegistrationEntity(
                                        e.id(),
                                        e.userId(),
                                        this.toEntityFromDomain(event))
                        )
                        .toList(),
                event.date(),
                event.cost(),
                event.duration(),
                event.locationId(),
                event.status()
        );
    }

    public EventSearch toDomainSearchFromDto(EventSearchRequestDto eventSearchRequestDto) {
        return new EventSearch(
                eventSearchRequestDto.name(),
                eventSearchRequestDto.placesMin(),
                eventSearchRequestDto.placesMax(),
                eventSearchRequestDto.dateStartAfter(),
                eventSearchRequestDto.dateStartBefore(),
                eventSearchRequestDto.costMin(),
                eventSearchRequestDto.costMax(),
                eventSearchRequestDto.durationMin(),
                eventSearchRequestDto.durationMax(),
                eventSearchRequestDto.locationId(),
                eventSearchRequestDto.eventStatus()
        );
    }

    public EventUpdate toDomainUpdateFromDto(EventUpdateRequestDto eventUpdateRequestDto) {
        return new EventUpdate(
                eventUpdateRequestDto.name(),
                eventUpdateRequestDto.maxPlaces(),
                eventUpdateRequestDto.date(),
                eventUpdateRequestDto.cost(),
                eventUpdateRequestDto.duration(),
                eventUpdateRequestDto.locationId()
        );
    }

    public Event withStatus(Event event,
                            EventStatus eventStatus
    ) {
        return new Event(
                event.id(),
                event.name(),
                event.ownerId(),
                event.maxPlaces(),
                event.registrationList(),
                event.date(),
                event.cost(),
                event.duration(),
                event.locationId(),
                eventStatus
        );
    }
}
