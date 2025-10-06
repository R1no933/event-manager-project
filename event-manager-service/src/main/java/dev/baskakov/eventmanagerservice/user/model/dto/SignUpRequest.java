package dev.baskakov.eventmanagerservice.user.model.dto;

public record SignUpRequest(
        String login,
        String password,
        Integer age
) {
}
