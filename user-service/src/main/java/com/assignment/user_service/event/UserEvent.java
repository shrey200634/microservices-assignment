package com.assignment.user_service.event;

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
    public static UserEvent of(String eventType, UUID userId, String email, String name) {
        return new UserEvent(
                UUID.randomUUID(),
                eventType,
                Instant.now(),
                userId,
                email,
                name
        );
    }
}