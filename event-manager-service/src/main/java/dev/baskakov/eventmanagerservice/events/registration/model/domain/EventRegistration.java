package dev.baskakov.eventmanagerservice.events.registration.model.domain;

public record EventRegistration(
        Long id,
        Long userId,
        Long eventId
) {
}
