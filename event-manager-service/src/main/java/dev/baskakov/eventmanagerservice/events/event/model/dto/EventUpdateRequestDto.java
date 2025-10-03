package dev.baskakov.eventmanagerservice.events.event.model.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

public record EventUpdateRequestDto(
        String name,

        @Min(0)
        Integer maxPlaces,

        @Future
        LocalDateTime date,

        @Min(0)
        Integer cost,

        @Min(30)
        Integer duration,

        Long locationId
) {
}
