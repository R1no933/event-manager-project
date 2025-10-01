package dev.baskakov.eventmanagerservice.events.registration;

public record EventRegistration(
        Long id,
        Long userId,
        Long eventId
) {
}
