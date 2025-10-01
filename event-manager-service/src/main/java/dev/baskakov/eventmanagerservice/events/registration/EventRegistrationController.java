package dev.baskakov.eventmanagerservice.events.registration;

import dev.baskakov.eventmanagerservice.events.event.EventController;
import dev.baskakov.eventmanagerservice.events.event.EventDto;
import dev.baskakov.eventmanagerservice.events.event.utils.EventConverter;
import dev.baskakov.eventmanagerservice.security.jwt.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events/registrations")
public class EventRegistrationController {

    private static final Logger log = LoggerFactory.getLogger(EventRegistrationController.class);

    private final EventRegistrationService eventRegistrationService;
    private final AuthenticationService authenticationService;
    private final EventConverter eventConverter;

    public EventRegistrationController(
            EventRegistrationService eventRegistrationService,
            AuthenticationService authenticationService,
            EventConverter eventConverter
    ) {
        this.eventRegistrationService = eventRegistrationService;
        this.authenticationService = authenticationService;
        this.eventConverter = eventConverter;
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> registrationOnEvent(
            @PathVariable("id") Long eventId
    ) {
        log.info("Received request to register event with id {}", eventId);

        var currentUser = authenticationService.getCurrentUser();
        eventRegistrationService.registerUserOnEvent(eventId, currentUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<Void> canceledRegistrationOnEvent(
            @PathVariable("id") Long eventId
    ) {
        log.info("Received request to cancel event with id {}", eventId);

        var currentUser = authenticationService.getCurrentUser();
        eventRegistrationService.cancelUserRegistrationOnEvent(eventId, currentUser);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<EventDto>> getMyEventsAlreadyRegistered() {
        log.info("Received request to get all already registred on events for user");

        var currentUser = authenticationService.getCurrentUser();
        var foundedEvents = eventRegistrationService.getAllRegisteretedEvents(currentUser.id());
        var responseEvents = foundedEvents
                .stream()
                .map(eventConverter::toDtoFromDomain)
                .toList();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseEvents);
    }

}
