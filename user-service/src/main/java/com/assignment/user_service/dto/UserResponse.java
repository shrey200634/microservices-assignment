package com.assignment.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "User profile response details")
@Getter
@Builder
public class UserResponse {

    @Schema(description = "Unique user identifier", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
    private UUID id;

    @Schema(description = "User full name", example = "John Doe")
    private String name;

    @Schema(description = "User email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Account creation timestamp in UTC", example = "2026-09-10T02:00:00Z")
    private Instant createdAt;
}