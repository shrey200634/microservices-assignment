package com.assignment.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterRequest {

    @NotBlank(message = "name is required")
    private String name ;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email ;


    @NotBlank(message = "pass is required")
    @Size(min = 5 , message = "pass must be at least 8 character ")
    private String password ;
}
