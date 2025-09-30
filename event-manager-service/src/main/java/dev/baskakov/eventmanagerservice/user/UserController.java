package dev.baskakov.eventmanagerservice.user;

import dev.baskakov.eventmanagerservice.security.jwt.AuthenticationService;
import dev.baskakov.eventmanagerservice.security.jwt.JwtTokenResponse;
import dev.baskakov.eventmanagerservice.user.utils.SignInRequest;
import dev.baskakov.eventmanagerservice.user.utils.SignUpRequest;
import dev.baskakov.eventmanagerservice.user.utils.UserConverter;
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
    private final UserConverter userConverter;

    public UserController(
            UserService userService,
            AuthenticationService authenticationService,
            UserConverter userConverter
    ) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.userConverter = userConverter;
    }

    @PostMapping
    public ResponseEntity<UserDto> registerUser(
            @RequestBody SignUpRequest signUpRequest
    ) {
        logger.info("Received request to register user {}", signUpRequest.login());
        var user = userService.registerUser(signUpRequest);
        var responseUser = userConverter.convertToDto(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseUser);
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
        logger.info("Received request to get user by id {}", id);
        var foundedUser = userService.findById(id);
        var responseUser = userConverter.convertToDto(foundedUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseUser);
    }
}
