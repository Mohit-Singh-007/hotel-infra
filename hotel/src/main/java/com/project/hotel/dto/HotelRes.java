package com.project.hotel.dto;

public record HotelRes(
        Long id,
        String name,
        String location,
        String description,
        Double rating
) {}
