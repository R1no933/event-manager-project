package dev.baskakov.eventmanagerservice;

import dev.baskakov.eventmanagerservice.user.UserRepository;
import dev.baskakov.eventmanagerservice.user.utils.DefaultUserInitialization;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@ConfigurationPropertiesScan
public class EventManagerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventManagerServiceApplication.class, args);
    }

}
