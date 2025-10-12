package dev.baskakov.eventnotificatorservice.controller;

import dev.baskakov.eventnotificatorservice.model.domain.NotificationResponse;
import dev.baskakov.eventnotificatorservice.model.dto.MarkAsReadRequestDTO;
import dev.baskakov.eventnotificatorservice.model.dto.NotificationResponseDTO;
import dev.baskakov.eventnotificatorservice.security.jwt.JwtTokenFilter;
import dev.baskakov.eventnotificatorservice.service.NotificationService;
import dev.baskakov.eventnotificatorservice.utils.Converter;
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
    private final Converter converter;


    public NotificationController(
            NotificationService notificationService,
            Converter converter
    ) {
        this.notificationService = notificationService;
        this.converter = converter;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getAllNotificationsForUser(
            @AuthenticationPrincipal JwtTokenFilter.SimpleUser currentUser
    ) {

        Long userId = currentUser.id();
        log.info("Getting all notifications for user {}", userId);
        try {
            List<NotificationResponse> notifications = notificationService.getNotificationsByUserId(userId);
            var notificationDTOList = notifications
                    .stream()
                    .map(converter::toDTO)
                    .toList();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(notificationDTOList);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> makeNotificationAsRead(
            @RequestBody MarkAsReadRequestDTO markAsReadRequestDTO
    ) {
        try {
            var markAsRequest = converter.toRequest(markAsReadRequestDTO);
            notificationService.makeNotificationAsRead(markAsRequest);
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
