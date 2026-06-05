package com.project.booking.dto;

import com.project.booking.models.BookingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookingRes(
        Long id,
        Long userId,
        Long roomId,
        Long hotelId,
        BookingStatus status,
        LocalDate checkIn,
        LocalDate checkOut,
        Double totalPrice,
        LocalDateTime createdAt
) {}
