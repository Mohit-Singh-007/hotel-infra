package com.project.booking.kafka;

import com.project.booking.dto.BookingEvent;
import com.project.booking.kafka.records.BookingCancelledEvent;
import com.project.booking.kafka.records.BookingConfirmedEvent;
import com.project.booking.models.Booking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingEventPublisher {
    private final BookingProducer bookingProducer;



    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBookingConfirmed(BookingConfirmedEvent event) {
        bookingProducer.publishBookingConfirmed(mapToKafkaEvent(event.booking()));
        log.info("booking.confirmed published for booking {}", event.booking().getId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBookingCancelled(BookingCancelledEvent event) {
        bookingProducer.publishBookingCancelled(mapToKafkaEvent(event.booking()));
        log.info("booking.cancelled published for booking {}", event.booking().getId());
    }

    private BookingEvent mapToKafkaEvent(Booking booking) {
        return new BookingEvent(
                booking.getId(),
                booking.getUserId(),
                booking.getRoomId(),
                booking.getHotelId(),
                booking.getStatus(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getTotalPrice(),
                LocalDateTime.now()
        );
    }
}
