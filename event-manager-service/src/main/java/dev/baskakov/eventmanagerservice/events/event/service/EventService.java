package dev.baskakov.eventmanagerservice.events.event.service;

import dev.baskakov.eventmanagerservice.events.event.model.EventStatus;
import dev.baskakov.eventmanagerservice.events.event.model.domain.Event;
import dev.baskakov.eventmanagerservice.events.event.model.dto.EventCreateRequestDto;
import dev.baskakov.eventmanagerservice.events.event.model.dto.EventSearchRequestDto;
import dev.baskakov.eventmanagerservice.events.event.model.dto.EventUpdateRequestDto;
import dev.baskakov.eventmanagerservice.events.event.repository.EventRepository;
import dev.baskakov.eventmanagerservice.events.event.utils.EventConverter;
import dev.baskakov.eventmanagerservice.location.model.domain.Location;
import dev.baskakov.eventmanagerservice.location.service.LocationService;
import dev.baskakov.eventmanagerservice.security.jwt.AuthenticationService;
import dev.baskakov.eventmanagerservice.user.model.UserRole;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

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
        log.info("EventService: createEvent - start with event to create: {}", eventToCreate);
        var location = locationService.getLocationById(eventToCreate.locationId());
        var currentUser = authenticationService.getCurrentUser();
        var domainEvent = eventConverter.toDomainFromCreateRequestDto(eventToCreate, currentUser.id());
        validateEventCreation(domainEvent, location);
        var entityEvent = eventConverter.toEntityFromDomain(domainEvent);
        entityEvent = eventRepository.save(entityEvent);
        return eventConverter.toDomainFromEntity(entityEvent);
    }

    public Event findEventById(Long id) {
        log.info("EventService: findEventById - start with event id: {}", id);
        var foundedEvent = eventRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event with id: " + id + " does not exist"));
        return eventConverter.toDomainFromEntity(foundedEvent);
    }

    public void cancelEventById(Long id) {
        log.info("EventService: cancelEventById - start with event id: {}", id);
        var event = findEventById(id);
        validateCanModifyEvent(event);
        validateEventCanBeCancelled(event);
        var cancelledEvent = eventConverter.withStatus(event, EventStatus.CANCELLED);
        var eventEntity = eventConverter.toEntityFromDomain(cancelledEvent);
        eventRepository.save(eventEntity);
    }

    public Event updateEventById(
            Long id,
            EventUpdateRequestDto eventToUpdateDto
    ) {
        log.info("EventService: updateEventById - start with event id: {}", id);
        var event = findEventById(id);
        var eventToUpdateDomain = eventConverter.toDomainFromUpdateDto(eventToUpdateDto, event);
        validateCanModifyEvent(event);
        validateCanUpdateEvent(event, eventToUpdateDomain);
        var updatedEvent = eventConverter.applyUpdateRequestDto(event, eventToUpdateDomain);
        var eventEntity = eventConverter.toEntityFromDomain(updatedEvent);
        eventEntity = eventRepository.save(eventEntity);
        return eventConverter.toDomainFromEntity(eventEntity);
    }

    public List<Event> searchByFilter(EventSearchRequestDto eventSearchFilter) {
        log.info("EventService: searchByFilter - start with event filter: {}", eventSearchFilter);
        var searchFilter = eventConverter.toDomainSearchFromDto(eventSearchFilter);
        var searchedEvents = eventRepository.searchEvents(
                searchFilter.name(),
                searchFilter.placesMin(),
                searchFilter.placesMax(),
                searchFilter.dateStartAfter(),
                searchFilter.dateStartBefore(),
                searchFilter.costMin(),
                searchFilter.costMax(),
                searchFilter.durationMin(),
                searchFilter.durationMax(),
                searchFilter.locationId(),
                searchFilter.eventStatus()
        );

        return searchedEvents
                .stream()
                .map(eventConverter::toDomainFromEntity)
                .toList();
    }

    public List<Event> searchEventsForCurrentUser() {
        log.info("EventService: searchEventsForCurrentUser - start!");
        var currentUser = authenticationService.getCurrentUser();
        var searchedEventList = eventRepository.findAllByOwnerIdIs(currentUser.id());
        return searchedEventList
                .stream()
                .map(eventConverter::toDomainFromEntity)
                .toList();
    }

    private void validateEventCreation(
            Event eventToCreate,
            Location location
    ) {
        if (location.capacity() < eventToCreate.maxPlaces()) {
            throw new IllegalArgumentException("Max places on current location is: %s, but need for event: %s"
                    .formatted(location.capacity(), eventToCreate.maxPlaces()));
        }
    }

    private void validateCanModifyEvent(
            Event event
    ) {
        var currentUser = authenticationService.getCurrentUser();
        if (!currentUser.role().equals(UserRole.ADMIN) && !currentUser.id().equals(event.ownerId())) {
            throw new IllegalArgumentException("Current user can't to modify event");
        }
    }

    private void validateEventCanBeCancelled(Event event) {
        if (event.status().equals(EventStatus.CANCELLED)) {
            throw new IllegalArgumentException("Can't cancel this event, because event already cancelled");
        }

        if (event.status().equals(EventStatus.FINISHED) || event.status().equals(EventStatus.STARTED)) {
            throw new IllegalArgumentException("Can't cancel this event, because event status is " + event.status());
        }
    }

    private void validateCanUpdateEvent(Event event, Event eventToUpdate) {
        if (!event.status().equals(EventStatus.WAIT_START)) {
            throw new IllegalArgumentException("Can't update this event, because event status is " + event.status());
        }
        if (eventToUpdate.maxPlaces() != null || eventToUpdate.locationId() != null) {
            Long  locationId = eventToUpdate.locationId() != null
                    ? eventToUpdate.locationId()
                    : event.locationId();
            Integer maxPlaces = eventToUpdate.maxPlaces() != null
                    ? eventToUpdate.maxPlaces()
                    : event.maxPlaces();
            var currentLocation = locationService.getLocationById(locationId);
            if (currentLocation.capacity() <  maxPlaces) {
                throw new IllegalArgumentException("Location capacity exceeded!");
            }
        }

        if (eventToUpdate.maxPlaces() != null && event.registrationList().size() < eventToUpdate.maxPlaces()) {
            throw new IllegalArgumentException("Can't reduced places below current registration!");
        }
    }
}
