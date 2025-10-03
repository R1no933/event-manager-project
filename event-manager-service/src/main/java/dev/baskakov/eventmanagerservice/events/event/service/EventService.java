package dev.baskakov.eventmanagerservice.events.event.service;

import dev.baskakov.eventmanagerservice.events.event.model.domain.Event;
import dev.baskakov.eventmanagerservice.events.event.model.dto.EventCreateRequestDto;
import dev.baskakov.eventmanagerservice.events.event.model.dto.EventSearchRequestDto;
import dev.baskakov.eventmanagerservice.events.event.model.dto.EventUpdateRequestDto;
import dev.baskakov.eventmanagerservice.events.event.repository.EventRepository;
import dev.baskakov.eventmanagerservice.events.event.utils.EventConverter;
import dev.baskakov.eventmanagerservice.location.service.LocationService;
import dev.baskakov.eventmanagerservice.security.jwt.AuthenticationService;
import dev.baskakov.eventmanagerservice.user.model.UserRole;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        var location = locationService.getLocationById(eventToCreate.locationId());
        var currentUser = authenticationService.getCurrentUser();
        var eventDomain = eventConverter.toDomainFromCreateRequestDto(eventToCreate, currentUser.id());
        var createdEvent = eventDomain.createEvent(
                eventDomain.name(),
                eventDomain.ownerId(),
                eventDomain.maxPlaces(),
                eventDomain.date(),
                eventDomain.cost(),
                eventDomain.duration(),
                eventDomain.locationId(),
                location
        );

        var eventEntity = eventConverter.toEntityFromDomain(createdEvent);
        eventRepository.save(eventEntity);
        log.info("Created event with id: {}", eventEntity.getId());
        return eventConverter.toDomainFromEntity(eventEntity);
    }

    public Event findEventById(Long id) {
        var foundedEvent = eventRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event with id: " + id + " does not exist"));
        return eventConverter.toDomainFromEntity(foundedEvent);
    }

    @Transactional
    public void cancelEventById(Long id) {
        checkUserBenefitsToModifyEvent(id);
        var event = findEventById(id);
        var canceledEvent = event.cancelEvent();
        var canceledEventEntity = eventConverter.toEntityFromDomain(canceledEvent);
        eventRepository.save(canceledEventEntity);
    }

    public Event updateEventById(
            Long id,
            EventUpdateRequestDto eventToUpdate
    ) {
        checkUserBenefitsToModifyEvent(id);
        var event = findEventById(id);
        var locationId = eventToUpdate.locationId() != null
                ? eventToUpdate.locationId() :
                event.locationId();
        var location = locationService.getLocationById(locationId);
        var updatedEvent = event.updateEvent(
                eventToUpdate.name(),
                eventToUpdate.maxPlaces(),
                eventToUpdate.date(),
                eventToUpdate.duration(),
                eventToUpdate.duration(),
                eventToUpdate.locationId(),
                location
        );
        var eventEntity = eventConverter.toEntityFromDomain(updatedEvent);
        eventEntity = eventRepository.save(eventEntity);

        return eventConverter.toDomainFromEntity(eventEntity);

    }

    public List<Event> searchByFilter(EventSearchRequestDto eventSearchFilter) {
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
