package dev.baskakov.eventmanagerservice.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;

public record UserDto(
        @Null
        Long id,

        @NotBlank
        String login,

        @Min(0)
        Integer age,

        @NotBlank
        UserRole role
) {
}
