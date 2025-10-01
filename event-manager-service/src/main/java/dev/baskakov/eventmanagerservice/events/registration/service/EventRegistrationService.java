package dev.baskakov.eventmanagerservice.events.registration.service;

import dev.baskakov.eventmanagerservice.events.event.model.EventStatus;
import dev.baskakov.eventmanagerservice.events.event.model.domain.Event;
import dev.baskakov.eventmanagerservice.events.event.repository.EventRepository;
import dev.baskakov.eventmanagerservice.events.event.service.EventService;
import dev.baskakov.eventmanagerservice.events.event.utils.EventConverter;
import dev.baskakov.eventmanagerservice.events.registration.repository.EventRegistrationRepository;
import dev.baskakov.eventmanagerservice.events.registration.model.entity.EventRegistrationEntity;
import dev.baskakov.eventmanagerservice.user.model.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventRegistrationService {

    private final EventRegistrationRepository eventRegistrationRepository;
    private final EventRepository eventRepository;
    private final EventService eventService;
    private final EventConverter eventConverter;

    public EventRegistrationService(
            EventRegistrationRepository eventRegistrationRepository,
            EventRepository eventRepository,
            EventService eventService,
            EventConverter eventConverter
    ) {
        this.eventRegistrationRepository = eventRegistrationRepository;
        this.eventRepository = eventRepository;
        this.eventService = eventService;
        this.eventConverter = eventConverter;
    }

    public void registerUserOnEvent(
            Long eventId,
            User user
    ) {
        var event = eventService.findEventById(eventId);

        if (!event.status().equals(EventStatus.WAIT_START)) {
            throw new IllegalArgumentException("Can't register user on event with status " + event.status());
        }

        if (user.id().equals(event.ownerId())) {
            throw new IllegalArgumentException("Owner user can't register on own event!");
        }

        var registration = eventRegistrationRepository
                .findExistsRegistration(user.id(), eventId);
        if (registration.isPresent()) {
            throw new IllegalArgumentException("User with id " + user.id() + " already registered on event with id " +  eventId);
        }

        eventRegistrationRepository.save(new EventRegistrationEntity(
                null,
                user.id(),
                eventRepository.findById(eventId).orElseThrow()
        ));
    }

    public void cancelUserRegistrationOnEvent(
            Long eventId,
            User user
    ) {
        var event = eventService.findEventById(eventId);
        var registration = eventRegistrationRepository
                .findExistsRegistration(user.id(), eventId);
        if (registration.isEmpty()) {
            throw new IllegalArgumentException("User with id " + user.id() + " haven't registration yet!");
        }

        if (!event.status().equals(EventStatus.WAIT_START)) {
            throw new IllegalArgumentException("Can't canceled register user on event with status " + event.status());
        }

        eventRegistrationRepository.delete(registration.orElseThrow());
    }

    public List<Event> getAllRegisteretedEvents(
            Long userId
    ) {
        var userEvents = eventRegistrationRepository.findRegisteredEventsForUser(userId);
        var responseEvents = userEvents
                .stream()
                .map(eventConverter::toDomainFromEntity)
                .toList();
        return responseEvents;
    }
}
