package dev.baskakov.eventmanagerservice.events.event.model.dto;

import dev.baskakov.eventmanagerservice.events.event.model.EventStatus;

import java.time.LocalDateTime;

public record EventSearchRequestDto(
    String name,
    Integer placesMin,
    Integer placesMax,
    LocalDateTime dateStartAfter,
    LocalDateTime dateStartBefore,
    Integer costMin,
    Integer costMax,
    Integer durationMin,
    Integer durationMax,
    Long locationId,
    EventStatus eventStatus
) {
}
