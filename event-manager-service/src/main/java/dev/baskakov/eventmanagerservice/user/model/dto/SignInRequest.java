package dev.baskakov.eventmanagerservice.user.model.dto;

public record SignInRequest(
        String login,
        String password
) {
}
