package com.project.payment.repo;

import com.project.payment.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepo extends JpaRepository<Payment,Long> {
    boolean existsByBookingId(Long bookingId);
    Optional<Payment> findByBookingId(Long bookingId);
}
