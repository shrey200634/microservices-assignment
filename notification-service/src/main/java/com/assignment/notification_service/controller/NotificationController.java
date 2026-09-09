package com.assignment.notification_service.controller;

import com.assignment.notification_service.config.OpenApiConfig;
import com.assignment.notification_service.dto.NotificationResponse;
import com.assignment.notification_service.repository.NotificationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Notifications", description = "Endpoints for viewing user notification history and audit logs")
@SecurityRequirement(name = OpenApiConfig.USER_ID_HEADER)
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Operation(summary = "Get notification history for user",
            description = "Retrieves a chronological list of all notifications sent to the specified user. The caller's identity is verified via the X-User-Id header injected by API Gateway.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notification history retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotificationResponse.class)))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Caller cannot access another user's notifications",
                    content = @Content)
    })
    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationResponse>> getHistory(
            @Parameter(description = "UUID of the user", required = true, example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
            @PathVariable UUID userId,
            @Parameter(description = "Caller user UUID (injected automatically by API Gateway)", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
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