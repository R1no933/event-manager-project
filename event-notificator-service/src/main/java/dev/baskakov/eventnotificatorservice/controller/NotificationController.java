package dev.baskakov.eventnotificatorservice.controller;

import dev.baskakov.eventnotificatorservice.model.dto.MarkAsReadRequestDTO;
import dev.baskakov.eventnotificatorservice.model.dto.NotificationResponseDTO;
import dev.baskakov.eventnotificatorservice.security.jwt.JwtTokenFilter;
import dev.baskakov.eventnotificatorservice.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getAllNotificationsForUser(
            @AuthenticationPrincipal JwtTokenFilter.SimpleUser currentUser
            ) {

        Long userId = currentUser.id();
        log.info("Getting all notifications for user {}", userId);

        try {
            List<NotificationResponseDTO> notifications = notificationService.getNotificationsByUserId(userId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(notifications);
        } catch (Exception e) {
            log.error("Error while getting all notifications for user {}", userId, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> makeNotificationAsRead(
            @RequestBody MarkAsReadRequestDTO markAsReadRequestDTO
            )
    {
        try {
            notificationService.makeNotificationAsRead(markAsReadRequestDTO);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .build();
        } catch (Exception e) {
            log.error("Error while making notification as read {}", markAsReadRequestDTO, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
