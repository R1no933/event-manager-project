package dev.baskakov.eventmanagerservice.user.utils;

import dev.baskakov.eventmanagerservice.user.UserEntity;
import dev.baskakov.eventmanagerservice.user.UserRepository;
import dev.baskakov.eventmanagerservice.user.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.awt.desktop.AppReopenedEvent;

@Component
public class DefaultUserInitialization {

    private static final Logger logger = LoggerFactory.getLogger(DefaultUserInitialization.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.defaultadmin.login}")
    private String adminLogin;
    @Value("${app.defaultadmin.password}")
    private String adminPassword;
    @Value("${app.defaultuser.login}")
    private String userLogin;
    @Value("${app.defaultuser.password}")
    private String userPassword;

    public DefaultUserInitialization(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createDefaultAdmin(ApplicationReadyEvent event) {
        try {
            if (!userRepository.existsByLogin(adminLogin)) {
                UserEntity admin = new UserEntity();
                admin.setLogin(adminLogin);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setAge(32);
                admin.setRole(UserRole.ADMIN.name());
                userRepository.save(admin);
                logger.info("Created default admin {}", admin);
            } else {
                logger.info("Default admin already exists");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createDefaultUser(ApplicationReadyEvent event) {
        try {
            if (!userRepository.existsByLogin(userLogin)) {
                UserEntity user = new UserEntity();
                user.setLogin(userLogin);
                user.setPassword(passwordEncoder.encode(userPassword));
                user.setAge(32);
                user.setRole(UserRole.USER.name());
                userRepository.save(user);
                logger.info("Created default users {}", user);
            } else {
                logger.info("Default user already exists");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
