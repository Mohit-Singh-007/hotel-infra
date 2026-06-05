package com.project.booking.dto;

public record AvailabilityRes(
        Long roomId,
        boolean available,
        Double pricePerNight
) {}
