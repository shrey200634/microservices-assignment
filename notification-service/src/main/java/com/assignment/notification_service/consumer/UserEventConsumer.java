package com.assignment.notification_service.consumer;

import com.assignment.notification_service.entity.Notification;
import com.assignment.notification_service.event.UserEvent;
import com.assignment.notification_service.notification.NotificationProvider;
import com.assignment.notification_service.repository.NotificationRepository;
import io.nats.client.*;
import io.nats.client.api.PublishAck;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.assignment.notification_service.config.NatsConfig.*;

@Component
public class UserEventConsumer {
    private final NotificationRepository notificationRepository;


    private static final Logger log = LoggerFactory.getLogger(UserEventConsumer.class);

    private final JetStream jetStream;
    private final ObjectMapper objectMapper;
    private final NotificationProvider notificationProvider;
    private final int maxDeliver;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean running = true;

    public UserEventConsumer(JetStream jetStream,
                             ObjectMapper objectMapper,
                             NotificationProvider notificationProvider,
                             NotificationRepository notificationRepository,
                             @Value("${notification.max-deliver}") int maxDeliver) {
        this.jetStream = jetStream;
        this.objectMapper = objectMapper;
        this.notificationProvider = notificationProvider;
        this.notificationRepository = notificationRepository;
        this.maxDeliver = maxDeliver;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        executor.submit(this::pollLoop);
        log.info("UserEventConsumer polling loop started");
    }

    @PreDestroy
    public void stop() {
        running = false;
        executor.shutdownNow();
    }

    private void pollLoop() {
        JetStreamSubscription sub;
        try {
            PullSubscribeOptions pullOptions = PullSubscribeOptions.bind(USER_EVENTS_STREAM, DURABLE_CONSUMER_NAME);
            sub = jetStream.subscribe(null, pullOptions);
        } catch (Exception e) {
            log.error("Failed to create pull subscription", e);
            return;
        }

        while (running) {
            try {
                List<Message> messages = sub.fetch(10, Duration.ofSeconds(5));
                for (Message msg : messages) {
                    handleMessage(msg);
                }
            } catch (Exception e) {
                if (running) {
                    log.error("Error during fetch loop", e);
                }
            }
        }
    }

    private void handleMessage(Message msg) {
        long deliveredCount = msg.metaData().deliveredCount();

        if (deliveredCount > maxDeliver) {
            log.error("Message exceeded max deliver attempts ({}), giving up on subject: {}",
                    maxDeliver, msg.getSubject());
            msg.ack();
            return;
        }

        UserEvent event = null;
        try {
            event = objectMapper.readValue(msg.getData(), UserEvent.class);
            notificationProvider.send(event);
            saveRecord(event, Notification.Status.SENT);
            msg.ack();
        } catch (Exception e) {
            log.warn("Failed to process message (attempt {}/{}), will retry: {}",
                    deliveredCount, maxDeliver, e.getMessage());
            if (event != null) {
                saveRecord(event, Notification.Status.FAILED);
            }
            msg.nak();
        }
    }

    private void saveRecord(UserEvent event, Notification.Status status) {
        Notification record = Notification.builder()
                .userId(event.userId())
                .eventType(event.eventType())
                .message(status == Notification.Status.SENT
                        ? "Notification sent for " + event.eventType()
                        : "Failed to send notification for " + event.eventType())
                .status(status)
                .sentAt(Instant.now())
                .build();
        notificationRepository.save(record);
    }

}