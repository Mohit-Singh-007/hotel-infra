package com.project.hotel.dto;

import com.project.hotel.models.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoomReq(
        @NotNull Long hotelId,
        @NotBlank String roomNumber,
        @NotNull RoomType type,
        @NotNull Double pricePerNight,
        @NotNull Integer maxOccupancy
) {}
