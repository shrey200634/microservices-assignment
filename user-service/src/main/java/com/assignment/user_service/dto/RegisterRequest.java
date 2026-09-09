package com.assignment.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Payload for registering a new user")
@Setter
@Getter
public class RegisterRequest {

    @Schema(description = "Full name of the user", example = "John Doe")
    @NotBlank(message = "name is required")
    private String name;

    @Schema(description = "Unique email address for the user", example = "john.doe@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @Schema(description = "User password (minimum 5 characters)", example = "SecurePass123!", accessMode = Schema.AccessMode.WRITE_ONLY)
    @NotBlank(message = "pass is required")
    @Size(min = 5, message = "pass must be at least 5 characters")
    private String password;
}
