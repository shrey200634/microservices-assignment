package com.assignment.notification_service.config;

import io.nats.client.JetStreamApiException;
import io.nats.client.JetStreamManagement;
import io.nats.client.api.AckPolicy;
import io.nats.client.api.ConsumerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static com.assignment.notification_service.config.NatsConfig.*;

@Configuration
public class ConsumerConfig {

    private static final Logger log = LoggerFactory.getLogger(ConsumerConfig.class);

    @Bean
    public ApplicationRunner ensureDurableConsumer(JetStreamManagement jsm) {
        return (ApplicationArguments args) -> {
            try {
                jsm.getConsumerInfo(USER_EVENTS_STREAM, DURABLE_CONSUMER_NAME);
                log.info("Durable consumer '{}' already exists", DURABLE_CONSUMER_NAME);
            } catch (JetStreamApiException e) {
                if (e.getApiErrorCode() == 10014) { // consumer not found
                    ConsumerConfiguration cc = ConsumerConfiguration.builder()
                            .durable(DURABLE_CONSUMER_NAME)
                            .ackPolicy(AckPolicy.Explicit)
                            .ackWait(Duration.ofSeconds(30))
                            .maxDeliver(5)
                            .filterSubjects("user.registered", "user.updated")
                            .build();
                    jsm.addOrUpdateConsumer(USER_EVENTS_STREAM, cc);
                    log.info("Created durable consumer '{}' on stream '{}'", DURABLE_CONSUMER_NAME, USER_EVENTS_STREAM);
                } else {
                    throw e;
                }
            }
        };
    }
}