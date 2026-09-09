package com.assignment.user_service.controller;

import com.assignment.user_service.dto.UpdateUserRequest;
import com.assignment.user_service.dto.UserResponse;
import com.assignment.user_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable UUID id,
                                @AuthenticationPrincipal UUID authenticatedUserId) {
        return userService.getById(id, authenticatedUserId);
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable UUID id,
                                   @AuthenticationPrincipal UUID authenticatedUserId,
                                   @Valid @RequestBody UpdateUserRequest request) {
        return userService.update(id, authenticatedUserId, request);
    }
}