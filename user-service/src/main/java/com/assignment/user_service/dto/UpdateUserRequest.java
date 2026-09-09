package com.assignment.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Payload for updating user profile information")
@Getter
@Setter
public class UpdateUserRequest {

    @Schema(description = "Updated full name of the user", example = "Johnathan Doe")
    @NotBlank(message = "name is required")
    private String name;

    @Schema(description = "Updated email address of the user", example = "john.doe.updated@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
}