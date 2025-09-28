package dev.baskakov.eventmanagerservice.user;

public record User(
        Long id,
        String login,
        Integer age,
        UserRole role
) {
}
