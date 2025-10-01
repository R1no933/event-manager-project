package dev.baskakov.eventmanagerservice.events.event.service;

import dev.baskakov.eventmanagerservice.events.event.model.EventStatus;
import dev.baskakov.eventmanagerservice.events.event.model.domain.Event;
import dev.baskakov.eventmanagerservice.events.event.model.dto.EventCreateRequestDto;
import dev.baskakov.eventmanagerservice.events.event.model.dto.EventSearchRequestDto;
import dev.baskakov.eventmanagerservice.events.event.model.dto.EventUpdateRequestDto;
import dev.baskakov.eventmanagerservice.events.event.model.entity.EventEntity;
import dev.baskakov.eventmanagerservice.events.event.repository.EventRepository;
import dev.baskakov.eventmanagerservice.events.event.utils.EventConverter;
import dev.baskakov.eventmanagerservice.location.service.LocationService;
import dev.baskakov.eventmanagerservice.security.jwt.AuthenticationService;
import dev.baskakov.eventmanagerservice.user.model.UserRole;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class.getName());

    private final EventRepository eventRepository;
    private final LocationService locationService;
    private final AuthenticationService authenticationService;
    private final EventConverter eventConverter;

    public EventService(EventRepository eventRepository,
                        LocationService locationService,
                        AuthenticationService authenticationService,
                        EventConverter eventConverter
    ) {
        this.eventRepository = eventRepository;
        this.locationService = locationService;
        this.authenticationService = authenticationService;
        this.eventConverter = eventConverter;
    }

    public Event createEvent(EventCreateRequestDto eventToCreate) {
        var location = locationService.getLocationById(eventToCreate.locationId());
        if (location.capacity() < eventToCreate.maxPlaces()) {
            throw new IllegalArgumentException("Max places on current location is: %s, but need for event: %s"
                    .formatted(location.capacity(), eventToCreate.maxPlaces()));
        }

        var currentUser = authenticationService.getCurrentUser();
        var eventEntity = new EventEntity(
                null,
                eventToCreate.name(),
                currentUser.id(),
                eventToCreate.maxPlaces(),
                List.of(),
                eventToCreate.date(),
                eventToCreate.cost(),
                eventToCreate.duration(),
                eventToCreate.locationId(),
                EventStatus.WAIT_START
        );

        eventEntity = eventRepository.save(eventEntity);
        log.info("Created event with id: {}", eventEntity.getId());

        return eventConverter.toDomainFromEntity(eventEntity);
    }

    public Event findEventById(Long id) {
        var foundedEvent = eventRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event with id: " + id + " does not exist"));
        return eventConverter.toDomainFromEntity(foundedEvent);
    }

    public void cancelEventById(Long id) {
        checkUserBenefitsToModifyEvent(id);
        var event = findEventById(id);

        if (event.status().equals(EventStatus.CANCELLED)) {
            log.info("Event with id: {} has been already cancelled", id);
            return;
        }

        if (event.status().equals(EventStatus.FINISHED) || event.status().equals(EventStatus.STARTED)) {
            throw new IllegalArgumentException("Can't cancel this event, because event status is " + event.status());
        }

        eventRepository.changeEventStatus(id, EventStatus.CANCELLED);
    }

    public Event updateEventById(
            Long id,
            EventUpdateRequestDto eventToUpdate
    ) {
        checkUserBenefitsToModifyEvent(id);
        var event = eventRepository.findById(id).orElseThrow();

        if (!event.getStatus().equals(EventStatus.WAIT_START)) {
            throw new IllegalArgumentException("Can't modify current event, because event status is " + event.getStatus());
        }

        if (eventToUpdate.maxPlaces() != null || eventToUpdate.locationId() != null) {
            var locationId = Optional.ofNullable(eventToUpdate.locationId()).orElse(event.getLocationId());
            var maxPlaces = Optional.ofNullable(eventToUpdate.maxPlaces()).orElse(event.getMaxPlaces());
            var location = locationService.getLocationById(locationId);
            if (location.capacity() < maxPlaces) {
                throw new IllegalArgumentException("Max places on current location is: %s, but need for event: %s"
                        .formatted(location.capacity(), maxPlaces));
            }
        }

        if (eventToUpdate.maxPlaces() != null && event.getRegistrationList().size() > eventToUpdate.maxPlaces()) {
            throw new IllegalArgumentException("Can't update, because need places %s, have places %s"
                    .formatted(event.getRegistrationList().size(), eventToUpdate.maxPlaces()));
        }

        Optional.ofNullable(eventToUpdate.name())
                .ifPresent(event::setName);
        Optional.ofNullable(eventToUpdate.maxPlaces())
                .ifPresent(event::setMaxPlaces);
        Optional.ofNullable(eventToUpdate.date())
                .ifPresent(event::setDate);
        Optional.ofNullable(eventToUpdate.cost())
                .ifPresent(event::setCost);
        Optional.ofNullable(eventToUpdate.duration())
                .ifPresent(event::setDuration);
        Optional.ofNullable(eventToUpdate.locationId())
                .ifPresent(event::setLocationId);

        eventRepository.save(event);

        return findEventById(id);

    }

    public List<Event> searchByFilter(EventSearchRequestDto eventSearchFilter) {
        var searchedEventList = eventRepository
                .searchEvents(
                        eventSearchFilter.name(),
                        eventSearchFilter.placesMin(),
                        eventSearchFilter.placesMax(),
                        eventSearchFilter.dateStartAfter(),
                        eventSearchFilter.dateStartBefore(),
                        eventSearchFilter.costMin(),
                        eventSearchFilter.costMax(),
                        eventSearchFilter.durationMin(),
                        eventSearchFilter.durationMax(),
                        eventSearchFilter.locationId(),
                        eventSearchFilter.eventStatus()
                );

        return searchedEventList
                .stream()
                .map(eventConverter::toDomainFromEntity)
                .toList();
    }

    public List<Event> searchEventsForCurrentUser() {
        var currentUser = authenticationService.getCurrentUser();
        var searchedEventList = eventRepository.findAllByOwnerIdIs(currentUser.id());
        return searchedEventList
                .stream()
                .map(eventConverter::toDomainFromEntity)
                .toList();
    }

    private void checkUserBenefitsToModifyEvent(Long eventId) {
        var currentUser = authenticationService.getCurrentUser();
        var currentEvent = findEventById(eventId);
        if (!currentUser.role().equals(UserRole.ADMIN)
                && !currentUser.id().equals(currentEvent.ownerId())) {
            throw new IllegalArgumentException("Current user can't modify this event");
        }
    }
}
