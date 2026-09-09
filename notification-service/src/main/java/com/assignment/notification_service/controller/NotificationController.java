package com.assignment.notification_service.controller;

import com.assignment.notification_service.dto.NotificationResponse;
import com.assignment.notification_service.repository.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationResponse>> getHistory(
            @PathVariable UUID userId,
            @RequestHeader(value = "X-User-Id", required = false) String callerId) {

        if (callerId == null || !callerId.equals(userId.toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<NotificationResponse> history = notificationRepository
                .findByUserIdOrderBySentAtDesc(userId)
                .stream()
                .map(NotificationResponse::from)
                .toList();

        return ResponseEntity.ok(history);
    }
}