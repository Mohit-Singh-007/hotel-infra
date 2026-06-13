package com.project.notification.kafka;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@Slf4j
public class KafkaConfig {
    @Bean
    public DefaultErrorHandler errorHandler() {
        return new DefaultErrorHandler(
                (record, exception) -> {
                    log.error("Failed to process record after retries: topic={} value={} error={}",
                            record.topic(),
                            record.value(),
                            exception.getMessage()
                    );
                },
                new FixedBackOff(1000L, 3L) // retry 3 times with 1s delay
        );
    }
}
