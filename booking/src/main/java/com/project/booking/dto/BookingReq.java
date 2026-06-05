package com.project.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BookingReq(
     @NotNull @NotBlank Long userId,
     @NotNull @NotBlank  Long hotelId,
     @NotNull @NotBlank  Long roomId,
     @NotNull @NotBlank  LocalDate checkIn,
     @NotNull @NotBlank  LocalDate checkOut
) {
}
