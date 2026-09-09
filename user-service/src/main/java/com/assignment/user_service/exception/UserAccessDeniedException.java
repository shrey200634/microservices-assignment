package com.assignment.user_service.exception;

public class UserAccessDeniedException extends RuntimeException {
    public UserAccessDeniedException() {
        super("You do not have permission to access this resource");
    }
}