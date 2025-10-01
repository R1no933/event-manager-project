package dev.baskakov.eventmanagerservice.user.utils;

import dev.baskakov.eventmanagerservice.user.model.entity.UserEntity;
import dev.baskakov.eventmanagerservice.user.repository.UserRepository;
import dev.baskakov.eventmanagerservice.user.model.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserInitialization {

    private static final Logger logger = LoggerFactory.getLogger(DefaultUserInitialization.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DefaultUserConfig defaultUserConfig;

    public DefaultUserInitialization(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            DefaultUserConfig defaultUserConfig
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.defaultUserConfig = defaultUserConfig;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createDefaultUsers(ApplicationReadyEvent event) {
        try {
            if (!userRepository.existsByLogin(defaultUserConfig.adminLogin())) {
                var admin = createDefaultUserEntity(
                        defaultUserConfig.adminLogin(),
                        defaultUserConfig.adminPassword(),
                        UserRole.ADMIN.name(),
                        35
                );
                logger.info("Created default admin {}", admin);
            } else {
                logger.info("Default admin already exists");
            }

            if (!userRepository.existsByLogin(defaultUserConfig.userLogin())) {
                var user = createDefaultUserEntity(
                        defaultUserConfig.userLogin(),
                        defaultUserConfig.userPassword(),
                        UserRole.USER.name(),
                        35
                );
                logger.info("Created default user {}", user);
            } else {
                logger.info("Default user already exists");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private UserEntity createDefaultUserEntity(
            String login, String password, String role, int age
    ) {
        UserEntity user = new UserEntity();
        user.setLogin(login);
        user.setPassword(passwordEncoder.encode(password));
        user.setAge(age);
        user.setRole(role);
        userRepository.save(user);
        return user;
    }
}
