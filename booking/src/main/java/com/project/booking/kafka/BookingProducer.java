package com.project.booking.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


/*
* use kafkaTemplate
* publish all events from config
* send the event
* */

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;


    public CompletableFuture<SendResult<String, String>> publish(String topic, String aggId, String payload) {
        return kafkaTemplate.send(topic, aggId, payload)
                .whenComplete((res, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish to topic {}: {}", topic, ex.getMessage());
                    } else {
                        log.info("Published to topic {} partition {} offset {}",
                                topic,
                                res.getRecordMetadata().partition(),
                                res.getRecordMetadata().offset()
                        );
                    }
                });
    }

}
