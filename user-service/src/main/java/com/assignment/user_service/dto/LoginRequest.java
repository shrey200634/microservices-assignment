package com.assignment.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Payload for authenticating and obtaining a JWT token")
@Getter
@Setter
public class LoginRequest {

    @Schema(description = "Registered email address", example = "john.doe@example.com")
    @NotBlank(message = "email is required")
    @Email(message = "email should be valid")
    private String email;

    @Schema(description = "User password", example = "SecurePass123!", accessMode = Schema.AccessMode.WRITE_ONLY)
    @NotBlank(message = "password is required")
    private String password;
}
