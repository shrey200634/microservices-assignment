package com.assignment.user_service.config;

import io.nats.client.*;
import io.nats.client.api.RetentionPolicy;
import io.nats.client.api.StorageType;
import io.nats.client.api.StreamConfiguration;
import io.nats.client.api.StreamInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Duration;

@Configuration
public class NatsConfig {

    public static final String STREAM_NAME = "USER_EVENTS";
    public static final String SUBJECT_REGISTERED = "user.registered";
    public static final String SUBJECT_UPDATED = "user.updated";

    @Value("${nats.url}")
    private String natsUrl;

    @Bean(destroyMethod = "close")
    public Connection natsConnection() throws IOException, InterruptedException {
        Options options = new Options.Builder()
                .server(natsUrl)
                .connectionTimeout(Duration.ofSeconds(5))
                .build();
        return Nats.connect(options);
    }

    @Bean
    public JetStream jetStream(Connection connection) throws IOException {
        return connection.jetStream();
    }

    @Bean
    public JetStreamManagement jetStreamManagement(Connection connection) throws IOException {
        return connection.jetStreamManagement();
    }

    /**
     * Ensures the USER_EVENTS stream exists before the app starts accepting
     * requests. Idempotent: if the stream already exists, this is a no-op.
     */
    @Bean
    public ApplicationRunner ensureUserEventsStream(JetStreamManagement jsm) {
        return (ApplicationArguments args) -> {
            try {
                jsm.getStreamInfo(STREAM_NAME);
                // Stream already exists — nothing to do.
            } catch (JetStreamApiException e) {
                if (e.getApiErrorCode() == 10059) { // stream not found
                    StreamConfiguration streamConfig = StreamConfiguration.builder()
                            .name(STREAM_NAME)
                            .subjects(SUBJECT_REGISTERED, SUBJECT_UPDATED)
                            .storageType(StorageType.File)
                            .retentionPolicy(RetentionPolicy.Limits)
                            .maxAge(Duration.ofDays(7))
                            .build();
                    StreamInfo info = jsm.addStream(streamConfig);
                    System.out.println("Created JetStream stream: " + info.getConfiguration().getName());
                } else {
                    throw e;
                }
            }
        };
    }
}