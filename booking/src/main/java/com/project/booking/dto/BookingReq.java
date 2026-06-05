package com.project.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BookingReq(
     @NotNull  Long userId,
     @NotNull   Long hotelId,
     @NotNull   Long roomId,
     @NotNull   LocalDate checkIn,
     @NotNull   LocalDate checkOut
) {
}
