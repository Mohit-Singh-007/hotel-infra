package com.project.payment.dto;

import com.project.payment.dto.utils.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentEvent(
        Long paymentId,
        Long bookingId,
        Long userId,
        Double amount,
        PaymentStatus status,
        String reason, // null -> success , failure reason
        LocalDateTime timestamp
) {
}
