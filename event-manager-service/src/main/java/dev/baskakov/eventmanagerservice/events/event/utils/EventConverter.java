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
import dev.baskakov.eventmanagerservice.kafka.EventNotificationMessage;
import dev.baskakov.eventmanagerservice.kafka.FieldChange;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

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
            Event existingEvent,
            Event updatinEvent
    ) {
        return new Event(
                existingEvent.id(),
                updatinEvent.name() != null
                        ? updatinEvent.name()
                        : existingEvent.name(),
                existingEvent.ownerId(),
                updatinEvent.maxPlaces() != null
                        ? updatinEvent.maxPlaces()
                        : existingEvent.maxPlaces(),
                existingEvent.registrationList(),
                updatinEvent.date() != null
                        ? updatinEvent.date()
                        : existingEvent.date(),
                updatinEvent.cost() != null
                        ? updatinEvent.cost()
                        : existingEvent.cost(),
                updatinEvent.duration() != null
                        ? updatinEvent.duration()
                        : existingEvent.duration(),
                updatinEvent.locationId() != null
                        ? updatinEvent.locationId()
                        : existingEvent.locationId(),
                existingEvent.status()
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
        EventEntity entity =  new EventEntity(
                event.id() != null ? event.id() : null,
                event.name(),
                event.ownerId(),
                event.maxPlaces(),
                Collections.emptyList(),
                event.date(),
                event.cost(),
                event.duration(),
                event.locationId(),
                event.status()
        );

        List<EventRegistrationEntity> eventRegistrationList = event.registrationList()
                .stream()
                .map(e -> new EventRegistrationEntity(
                        e.id(),
                        e.userId(),
                        entity
                ))
                .toList();

        entity.setRegistrationList(eventRegistrationList);

        return entity;
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

    public Event toDomainFromUpdateDto(
            EventUpdateRequestDto eventUpdateRequestDto,
            Event existingEvent
    ) {
        return new Event(
                existingEvent.id(),
                eventUpdateRequestDto.name(),
                existingEvent.ownerId(),
                eventUpdateRequestDto.maxPlaces(),
                existingEvent.registrationList(),
                eventUpdateRequestDto.date(),
                eventUpdateRequestDto.cost(),
                eventUpdateRequestDto.duration(),
                eventUpdateRequestDto.locationId(),
                existingEvent.status()
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

    public EventNotificationMessage toEventNotificationMessage(
            Event beforeUpdate,
            Event afterUpdate,
            Long userId
    ) {
        List<Long> usersIds = afterUpdate.registrationList() == null
                ? List.of()
                : afterUpdate.registrationList()
                .stream()
                .map(EventRegistration::eventId)
                .toList();

        return new EventNotificationMessage(
                beforeUpdate.id(),
                usersIds,
                beforeUpdate.ownerId(),
                userId,
                createFieldChange(beforeUpdate, afterUpdate, Event::name),
                createFieldChange(beforeUpdate, afterUpdate, Event::maxPlaces),
                createFieldChange(beforeUpdate, afterUpdate, Event::date),
                createFieldChange(beforeUpdate, afterUpdate, Event::cost),
                createFieldChange(beforeUpdate, afterUpdate, Event::duration),
                createFieldChange(beforeUpdate, afterUpdate, Event::locationId),
                createFieldChange(beforeUpdate, afterUpdate, Event::status)
        );
    }

    private <T> FieldChange<T> createFieldChange(
            Event beforeUpdate,
            Event afterUpdate,
            Function<Event, T> fieldExtractor
    ) {
        T before = beforeUpdate != null
                ? fieldExtractor.apply(beforeUpdate)
                : null;
        T after = afterUpdate != null
                ? fieldExtractor.apply(afterUpdate)
                : null;

        if (Objects.equals(before, after)) {
            return null;
        }
        return new FieldChange<>(before, after);
    }
}
