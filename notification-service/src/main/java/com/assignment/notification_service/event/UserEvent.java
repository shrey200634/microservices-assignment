package com.assignment.notification_service.event;

import java.time.Instant;
import java.util.UUID;

public record UserEvent(
        UUID eventId,
        String eventType,
        Instant occurredAt,
        UUID userId,
        String email,
        String name
) {
}