package com.project.booking.kafka.records;

import com.project.booking.models.Booking;

public record BookingConfirmedEvent(
        Booking booking
) {
}
