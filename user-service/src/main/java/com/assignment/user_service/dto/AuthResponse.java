package com.assignment.user_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String token ;
    private UserResponse user;
}
