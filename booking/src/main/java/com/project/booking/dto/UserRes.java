package com.project.booking.dto;

public record UserRes(
        Long id,
        String name,
        String email,
        String role
) {}
