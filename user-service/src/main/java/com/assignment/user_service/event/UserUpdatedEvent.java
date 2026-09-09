package com.assignment.user_service.event;

import com.assignment.user_service.event.UserEvent;

public record UserUpdatedEvent(UserEvent payload) {
}