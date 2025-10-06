package dev.baskakov.eventmanagerservice.user.service;

import dev.baskakov.eventmanagerservice.user.model.domain.User;
import dev.baskakov.eventmanagerservice.user.repository.UserRepository;
import dev.baskakov.eventmanagerservice.user.model.dto.SignUpRequest;
import dev.baskakov.eventmanagerservice.user.utils.UserConverter;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserConverter userConverter;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserConverter userConverter
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userConverter = userConverter;
    }
    public User registerUser(
            SignUpRequest signUpRequest
    ) {
        if (userRepository.existsByLogin(signUpRequest.login())) {
            throw new IllegalArgumentException("Login already exists! Login: " + signUpRequest.login());
        }
        var user = userConverter.toDomainFromSignUpRequest(signUpRequest);
        var hashedPassword = passwordEncoder.encode(signUpRequest.password());
        var userEntity = userConverter.convertToEntityWithPassword(user, hashedPassword);
        var saved = userRepository.save(userEntity);

        return userConverter.convertToDomain(saved);
    }


    public User findByLogin(String login) {
        var user = userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("User with login " + login + " not found"));

        return userConverter.convertToDomain(user);
    }

    public User findById(Long id) {
        var foundedUSer = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));

        return userConverter.convertToDomain(foundedUSer);
    }
}
