package com.project.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserReq(
       @NotBlank String name,
       @Email @NotBlank String email,
       @NotBlank String password
) {}
