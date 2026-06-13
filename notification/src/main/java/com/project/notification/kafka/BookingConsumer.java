package com.project.notification.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.notification.dto.BookingEvent;
import com.project.notification.models.NotificationLogs;
import com.project.notification.repo.NotificationRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingConsumer {
    private final NotificationRepo repo;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopics.BOOKING_CREATED,groupId = "notification-group")
    public void onBookingCreated(BookingEvent event){
        log.info("[NOTIFICATION]Booking created - userID:{} roomId: {} total: {}",event.userId(),event.roomId(),event.totalPrice());
        saveLog(event,KafkaTopics.BOOKING_CREATED,"Your booking has been created for room: "+event.roomId());

    }


    @KafkaListener(topics = KafkaTopics.BOOKING_CONFIRMED,groupId = "notification-group")
    public void onBookingConfirmed(String msg) {

        try {
            BookingEvent event = objectMapper.readValue(msg, BookingEvent.class);

            if(repo.existsByBookingIdAndType(event.bookingId(),"BOOKING_CONFIRMED")){
                log.warn("Duplicate event ignored: bookingId={}", event.bookingId());
                return;
            }

            log.info("[NOTIFICATION] Booking confirmed — bookingId={} userId={}", event.bookingId(), event.userId());
            saveLog(event, KafkaTopics.BOOKING_CONFIRMED, "Your booking " + event.bookingId() + " is confirmed! Total: " + event.totalPrice());


        }catch (Exception e){
            log.error("Failed to process booking confirmed event: {}", e.getMessage());
            throw new RuntimeException(e); // rethrow so DefaultErrorHandler retries
        }
    }

    @KafkaListener(topics = KafkaTopics.BOOKING_CANCELLED, groupId = "notification-group")
    public void onBookingCancelled(BookingEvent event) {
        log.info("[NOTIFICATION] Booking cancelled — bookingId={} userId={}",
                event.bookingId(), event.userId());

        saveLog(event, KafkaTopics.BOOKING_CANCELLED, "Your booking " + event.bookingId() + " has been cancelled");
    }


    private void saveLog(BookingEvent event,String type,String msg){
        repo.save(
                NotificationLogs.builder()
                        .bookingId(event.bookingId())
                        .userId(event.userId())
                        .type(type)
                        .message(msg)
                        .build()
        );
    }

}
