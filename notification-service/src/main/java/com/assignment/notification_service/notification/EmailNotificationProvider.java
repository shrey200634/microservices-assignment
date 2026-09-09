package com.assignment.notification_service.notification;

import com.assignment.notification_service.event.UserEvent;
import com.assignment.notification_service.notification.NotificationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationProvider implements NotificationProvider {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationProvider.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    public EmailNotificationProvider(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(UserEvent event) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(event.email());
            message.setSubject(subjectFor(event.eventType()));
            message.setText(bodyFor(event));

            mailSender.send(message);

            log.info("Email sent to {} for event {} (eventId: {})",
                    event.email(), event.eventType(), event.eventId());

        } catch (Exception e) {
            log.error("Failed to send email to {} for eventId {}", event.email(), event.eventId(), e);
            throw e; // rethrow so UserEventConsumer's catch triggers a NAK/retry
        }
    }

    private String subjectFor(String eventType) {
        return switch (eventType) {
            case "user.registered" -> "Welcome!";
            case "user.updated" -> "Your profile was updated";
            default -> "Notification";
        };
    }

    private String bodyFor(UserEvent event) {
        return switch (event.eventType()) {
            case "user.registered" -> "Hi " + event.name() + ",\n\nYour account has been created successfully.";
            case "user.updated" -> "Hi " + event.name() + ",\n\nYour profile details were just updated.";
            default -> "Hi " + event.name() + ",\n\nYou have a new notification.";
        };
    }
}