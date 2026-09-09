package com.assignment.notification_service.dto;

import com.assignment.notification_service.entity.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Notification record audit details")
@Getter
@Builder
public class NotificationResponse {

    @Schema(description = "Unique notification identifier", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    private UUID id;

    @Schema(description = "Originating event type", example = "user.registered")
    private String eventType;

    @Schema(description = "Notification message content or subject", example = "Welcome to our platform, John Doe!")
    private String message;

    @Schema(description = "Delivery status of the notification", example = "SENT")
    private Notification.Status status;

    @Schema(description = "Timestamp when the notification was processed/sent in UTC", example = "2026-09-10T02:00:00Z")
    private Instant sentAt;

    public static NotificationResponse from(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .eventType(n.getEventType())
                .message(n.getMessage())
                .status(n.getStatus())
                .sentAt(n.getSentAt())
                .build();
    }
}