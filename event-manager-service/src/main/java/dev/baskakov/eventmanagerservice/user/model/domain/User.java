package dev.baskakov.eventmanagerservice.user.model.domain;

import dev.baskakov.eventmanagerservice.user.model.UserRole;

public record User(
        Long id,
        String login,
        Integer age,
        UserRole role
) {
}
