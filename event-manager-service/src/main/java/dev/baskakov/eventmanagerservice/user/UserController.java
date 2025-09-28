package dev.baskakov.eventmanagerservice.user;

import dev.baskakov.eventmanagerservice.security.jwt.AuthenticationService;
import dev.baskakov.eventmanagerservice.security.jwt.JwtTokenResponse;
import dev.baskakov.eventmanagerservice.user.utils.SignInRequest;
import dev.baskakov.eventmanagerservice.user.utils.SignUpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public UserController(
            UserService userService,
            AuthenticationService authenticationService
    ) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @PostMapping
    public ResponseEntity<UserDto> registerUser(
            @RequestBody SignUpRequest signUpRequest
    ) {
        logger.info("Received request to register user {}", signUpRequest.login());
        var user = userService.registerUser(signUpRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new UserDto(
                        user.id(),
                        user.login(),
                        user.age(),
                        user.role()
                ));
    }

    @PostMapping("/auth")
    public ResponseEntity<JwtTokenResponse> authenticateUser(
            @RequestBody SignInRequest signInRequest
    ) {
        logger.info("Received request to authenticate user {}", signInRequest.login());
        var token = authenticationService.authenticateUser(signInRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new JwtTokenResponse(token));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(
            @PathVariable Long id
    ){
        var foundedUser = userService.findById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new UserDto(
                        foundedUser.id(),
                        foundedUser.login(),
                        foundedUser.age(),
                        foundedUser.role()
                ));
    }
}
