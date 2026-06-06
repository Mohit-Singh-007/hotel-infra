package com.project.notification.exceptions;

import java.time.LocalDateTime;

public record ApiError(
        int status,
        String error,
        String message,
        LocalDateTime timeStamp
) {}
