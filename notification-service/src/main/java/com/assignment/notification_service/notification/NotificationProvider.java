package com.assignment.notification_service.notification;

import com.assignment.notification_service.event.UserEvent;

public interface NotificationProvider {
    void send(UserEvent event);
}