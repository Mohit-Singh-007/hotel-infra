package com.project.booking.exceptions;

import java.time.LocalDateTime;

public record ApiError(
        int status,
        String error,
        String message,
        LocalDateTime timeStamp
) {}
