package com.project.hotel.dto;

import com.project.hotel.models.RoomType;

public record RoomRes(
        Long id,
        Long hotelId,
        String roomNumber,
        RoomType type,
        Double pricePerNight,
        boolean available,
        Integer maxOccupancy
) {}
