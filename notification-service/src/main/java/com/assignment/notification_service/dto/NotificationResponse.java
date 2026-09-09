package com.assignment.notification_service.dto;

import com.assignment.notification_service.entity.Notification;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class NotificationResponse {
    private UUID id;
    private String eventType;
    private String message;
    private Notification.Status status;
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