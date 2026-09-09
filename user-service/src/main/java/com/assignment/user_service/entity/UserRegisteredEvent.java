package com.assignment.user_service.entity;

import com.assignment.user_service.event.UserEvent;

public record UserRegisteredEvent(UserEvent payload) {
}