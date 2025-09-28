package dev.baskakov.eventmanagerservice.user.utils;

public record SignUpRequest(
        String login,
        String password,
        Integer age
) {
}
