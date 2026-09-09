package com.assignment.user_service.event;

import com.assignment.user_service.entity.UserRegisteredEvent;
import io.nats.client.JetStream;
import io.nats.client.PublishOptions;
import io.nats.client.api.PublishAck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tools.jackson.databind.ObjectMapper;

@Component
public class NatsEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(NatsEventPublisher.class);

    private final JetStream jetStream;
    private final ObjectMapper objectMapper;

    public NatsEventPublisher(JetStream jetStream, ObjectMapper objectMapper) {
        this.jetStream = jetStream;
        this.objectMapper = objectMapper;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserRegistered(UserRegisteredEvent event) {
        UserEvent payload = event.payload();
        try {
            byte[] body = objectMapper.writeValueAsBytes(payload);

            PublishAck ack = jetStream.publish(
                    payload.eventType(),
                    body,
                    PublishOptions.builder()
                            .messageId(payload.eventId().toString())
                            .build()
            );

            log.info("Published {} event for user {} — stream seq {}",
                    payload.eventType(), payload.userId(), ack.getSeqno());

        } catch (Exception e) {
            // We're intentionally NOT rethrowing here. The DB transaction already
            // committed (this listener only fires AFTER_COMMIT) — the user is
            // registered either way. A NATS publish failure here should be logged
            // and alerted on, not surfaced as a 500 to a user who already succeeded.
            log.error("Failed to publish {} event for user {}", payload.eventType(), payload.userId(), e);
        }
    }



    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserUpdated(UserUpdatedEvent event) {
        UserEvent payload = event.payload();
        try {
            byte[] body = objectMapper.writeValueAsBytes(payload);
            PublishAck ack = jetStream.publish(
                    payload.eventType(),
                    body,
                    PublishOptions.builder()
                            .messageId(payload.eventId().toString())
                            .build()
            );
            log.info("Publish {} event for user {} — stream seq {}",
                    payload.eventType(), payload.userId(), ack.getSeqno());
        } catch (Exception e) {
            log.error("Failed to publish {} event for user {}", payload.eventType(), payload.userId(), e);
        }
    }
}