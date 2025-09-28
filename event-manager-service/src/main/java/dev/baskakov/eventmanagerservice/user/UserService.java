package dev.baskakov.eventmanagerservice.user;

import dev.baskakov.eventmanagerservice.user.utils.SignUpRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public User registerUser(
            SignUpRequest signUpRequest
    ) {
        if (userRepository.existsByLogin(signUpRequest.login())) {
            throw new IllegalArgumentException("Login already exists! Login: " + signUpRequest.login());
        }

        var hashedPassword = passwordEncoder.encode(signUpRequest.password());
        var newUser = new UserEntity(
                null,
                signUpRequest.login(),
                hashedPassword,
                signUpRequest.age()
        );

        var saved = userRepository.save(newUser);

        return new User(
                saved.getId(),
                saved.getLogin(),
                saved.getAge(),
                UserRole.valueOf(saved.getRole())
        );
    }


    public User findByLogin(String login) {
        var user = userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("User with login " + login + " not found"));

        return new User(
                user.getId(),
                user.getLogin(),
                user.getAge(),
                UserRole.valueOf(user.getRole())
        );
    }

    public User findById(Long id) {
        var foundedUSer = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
        return new User(
                foundedUSer.getId(),
                foundedUSer.getLogin(),
                foundedUSer.getAge(),
                UserRole.valueOf(foundedUSer.getRole())
        );
    }
}
