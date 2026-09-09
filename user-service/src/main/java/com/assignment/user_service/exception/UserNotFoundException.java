package com.assignment.user_service.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID id) {
        super("No user found with id '" + id + "'");
    }
}