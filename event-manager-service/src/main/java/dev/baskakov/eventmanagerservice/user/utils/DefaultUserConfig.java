package dev.baskakov.eventmanagerservice.user.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.default")
public record DefaultUserConfig(
        String adminLogin,
        String adminPassword,
        String userLogin,
        String userPassword
) {
}
