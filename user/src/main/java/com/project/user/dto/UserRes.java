package com.project.user.dto;

import com.project.user.model.Role;

public record UserRes(
        Long id,
        String name,
        String email,
        String role
) {}
