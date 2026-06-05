package com.project.hotel.dto;

import jakarta.validation.constraints.NotBlank;

public record HotelReq(
       @NotBlank String name,
       @NotBlank String location,
        String description,
        Double rating
) {}
