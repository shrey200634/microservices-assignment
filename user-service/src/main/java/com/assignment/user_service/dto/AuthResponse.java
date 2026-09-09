package com.assignment.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Authentication response containing JWT token and user profile")
@Getter
@Setter
public class AuthResponse {

    @Schema(description = "Signed JSON Web Token (JWT) to include in Authorization header as Bearer token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Authenticated user profile details")
    private UserResponse user;
}
