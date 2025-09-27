package dev.baskakov.eventmanagerservice.location;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public record LocationDTO(
        @Null
        Long id,

        @NotBlank
        String name,

        @NotBlank
        String address,

        @Min(5)
        @NotNull
        Integer capacity,

        String description
) {
}
