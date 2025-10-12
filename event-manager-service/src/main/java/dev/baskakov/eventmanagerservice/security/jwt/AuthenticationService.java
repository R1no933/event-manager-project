package dev.baskakov.eventmanagerservice.security.jwt;

import dev.baskakov.eventmanagerservice.user.model.UserRole;
import dev.baskakov.eventmanagerservice.user.model.domain.User;
import dev.baskakov.eventmanagerservice.user.model.dto.SignInRequest;
import dev.baskakov.eventmanagerservice.user.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {


    private final AuthenticationManager authenticationManager;
    private final JwtTokenManager jwtTokenManager;
    private final UserService userService;

    public AuthenticationService(
            AuthenticationManager authenticationManager,
            JwtTokenManager jwtTokenManager,
            UserService userService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenManager = jwtTokenManager;
        this.userService = userService;
    }


    public String authenticateUser(SignInRequest signInRequest) {
        authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                signInRequest.login(),
                                signInRequest.password()
                        )
                );
        var user = userService.findByLogin(signInRequest.login());

        return jwtTokenManager.generateToken(
                signInRequest.login(),
                user.id(),
                user.role()
        );
    }

    public User getCurrentUser() {
        var authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null) {
            throw new IllegalArgumentException("Current user is not authenticated");
        }

        return (User) authentication.getPrincipal();
    }
}
