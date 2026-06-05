package com.project.booking.kafka;

import com.project.booking.dto.BookingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


/*
* use kafkaTemplate
* publish all events from config
* send the event
* */

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingProducer {
    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;

    public void publishBookingCreated(BookingEvent event){
        publish(KafkaConfig.BOOKING_CREATED,event);
    }
    public void publishBookingCancelled(BookingEvent event){
        publish(KafkaConfig.BOOKING_CANCELLED,event);
    }
    public void publishBookingConfirmed(BookingEvent event){
        publish(KafkaConfig.BOOKING_CONFIRMED,event);
    }

    private void publish(String topic, BookingEvent event){
        kafkaTemplate.send(topic,event.bookingId().toString(),event)
                .whenComplete((res,ex) ->{
                    if(ex != null){
                        log.error("Failed to publish to topic {}: {}", topic, ex.getMessage());
                    }else{
                        log.info("Published to topic {} partition {} offset {}",
                                topic,
                                res.getRecordMetadata().partition(),
                                res.getRecordMetadata().offset()
                        );

                    }
                });
    }

}
