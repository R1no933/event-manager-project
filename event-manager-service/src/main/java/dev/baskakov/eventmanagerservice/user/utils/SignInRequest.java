package dev.baskakov.eventmanagerservice.user.utils;

public record SignInRequest(
        String login,
        String password
) {
}
