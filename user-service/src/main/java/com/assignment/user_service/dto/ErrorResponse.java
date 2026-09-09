package com.assignment.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Schema(description = "Standardized error response structure")
@Getter
@Builder
public class ErrorResponse {

    @Schema(description = "Timestamp when error occurred", example = "2026-09-10T02:00:00Z")
    private Instant timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "HTTP error reason phrase", example = "Bad Request")
    private String error;

    @Schema(description = "Detailed human-readable error explanation", example = "Email already registered")
    private String message;
}