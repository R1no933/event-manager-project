package dev.baskakov.eventmanagerservice.user.service;

import dev.baskakov.eventmanagerservice.user.model.entity.UserEntity;
import dev.baskakov.eventmanagerservice.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("User not found with login: " + login));

        return User.withUsername(login)
                .password(user.getPassword())
                .authorities(user.getRole())
                .build();
    }
}
