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
    public Event createEvent(
            String name, Long ownerId, Integer maxPlaces,
            LocalDateTime date, Integer cost, Integer duration, Long locationId, Location location
    ) {
        if (location.capacity() < maxPlaces) {
            throw new IllegalArgumentException("Max places on current location is: %s, but need for event: %s"
                    .formatted(location.capacity(), maxPlaces));
        }

        return new Event(
                null,
                name,
                ownerId,
                maxPlaces,
                List.of(),
                date,
                cost,
                duration,
                locationId,
                EventStatus.WAIT_START
        );

    }

    public Event cancelEvent() {
        if (this.status.equals(EventStatus.CANCELLED)) {
            throw new IllegalStateException("Event with id: " + id + " has been already cancelled cancelled");
        }

        if (this.status.equals(EventStatus.STARTED) || this.status.equals(EventStatus.FINISHED)) {
            throw new IllegalStateException("Event with status: " + this.status.name() + " can't be cancelled");
        }

        return new Event(
                this.id,
                this.name,
                this.ownerId,
                this.maxPlaces,
                this.registrationList,
                this.date,
                this.cost,
                this.duration,
                this.locationId,
                EventStatus.CANCELLED
        );
    }

    public Event updateEvent(
            String name, Integer maxPlaces, LocalDateTime date,
            Integer cost, Integer duration, Long locationId, Location location
    ) {
        if (!this.status.equals(EventStatus.WAIT_START)) {
            throw new IllegalArgumentException("Can't modify current event, because event status is " + this.status);
        }

        if (maxPlaces != null && location.capacity() < maxPlaces) {
            throw new IllegalArgumentException("Max places on current location is: %s, but need for event: %s"
                    .formatted(location.capacity(), maxPlaces));
        }

        if (maxPlaces != null && this.registrationList().size() > maxPlaces) {
            throw new IllegalArgumentException("Can't update, because need places %s, have places %s"
                    .formatted(this.registrationList().size(), maxPlaces));
        }

        return new Event(
                this.id,
                name != null ? name : this.name,
                this.ownerId,
                maxPlaces != null ? maxPlaces : this.maxPlaces,
                this.registrationList,
                date != null ? date : this.date,
                cost != null ? cost : this.cost,
                duration != null ? duration : this.duration,
                locationId != null ? locationId : this.locationId,
                this.status
        );
    }
}
