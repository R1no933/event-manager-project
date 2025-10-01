package dev.baskakov.eventmanagerservice.events.event;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record EventCreateRequestDto(
        @NotBlank
        String name,

        @NotNull
        @Positive
        Integer maxPlaces,

        @NotNull
        @Future
        LocalDateTime date,

        @Min(0)
        @NotNull
        Integer cost,

        @Min(30)
        @NotNull
        Integer duration,

        @NotNull
        Long locationId
) {
}
