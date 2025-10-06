package dev.baskakov.eventmanagerservice.events.event.controller;

import dev.baskakov.eventmanagerservice.events.event.model.domain.Event;
import dev.baskakov.eventmanagerservice.events.event.model.dto.EventCreateRequestDto;
import dev.baskakov.eventmanagerservice.events.event.model.dto.EventDto;
import dev.baskakov.eventmanagerservice.events.event.model.dto.EventSearchRequestDto;
import dev.baskakov.eventmanagerservice.events.event.model.dto.EventUpdateRequestDto;
import dev.baskakov.eventmanagerservice.events.event.service.EventService;
import dev.baskakov.eventmanagerservice.events.event.utils.EventConverter;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {
    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    private final EventService eventService;
    private final EventConverter eventConverter;

    public EventController(EventService eventService,
                           EventConverter eventConverter
    ) {
        this.eventService = eventService;
        this.eventConverter = eventConverter;
    }

    @PostMapping
    public ResponseEntity<EventDto> createEvent(
            @RequestBody @Valid EventCreateRequestDto eventToCreate
    ) {
        log.info("Received request to Create Event: {}", eventToCreate);
        Event requsetEvent = eventService.createEvent(eventToCreate);
        EventDto responseEvent = eventConverter.toDtoFromDomain(requsetEvent);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseEvent);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEventById(
            @PathVariable Long id
    ) {
        log.info("Received request to Get Event with id: {}", id);
        Event foundedEvent = eventService.findEventById(id);
        EventDto responseEvent = eventConverter.toDtoFromDomain(foundedEvent);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelEvent(
            @PathVariable Long id
    ) {
        log.info("Received request to cancel Event with id: {}", id);
        eventService.cancelEventById(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDto> updateEvent(
            @PathVariable("id") Long id,
            @RequestBody @Valid EventUpdateRequestDto eventToUpdate
    ) {
        log.info("Received request to update Event with id: {}, and request {}", id, eventToUpdate);
        var updatedEvent = eventService.updateEventById(id, eventToUpdate);
        var responseEvent = eventConverter.toDtoFromDomain(updatedEvent);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseEvent);
    }

    @PostMapping("/search")
    public ResponseEntity<List<EventDto>> searchEvent(
            @RequestBody @Valid EventSearchRequestDto eventSearchFilter
    ) {
        log.info("Received request to search Events by parameters {}", eventSearchFilter);
        var foundedEvents = eventService.searchByFilter(eventSearchFilter);
        var responseEvents = foundedEvents
                .stream()
                .map(eventConverter::toDtoFromDomain)
                .toList();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseEvents);
    }

    @GetMapping("/my")
    public ResponseEntity<List<EventDto>> getMyEvent() {
        log.info("Received request to get all events for current user");
        var foundedEvents = eventService.searchEventsForCurrentUser();
        var responseEvents = foundedEvents
                .stream()
                .map(eventConverter::toDtoFromDomain)
                .toList();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseEvents);
    }
}
