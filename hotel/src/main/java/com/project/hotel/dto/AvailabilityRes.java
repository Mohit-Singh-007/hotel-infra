package com.project.hotel.dto;

// to check available or not
public record AvailabilityRes(
        Long roomId,
        boolean available,
        Double pricePerNight
) {}
