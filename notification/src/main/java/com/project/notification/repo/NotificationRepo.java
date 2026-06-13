package com.project.notification.repo;

import com.project.notification.dto.BookingStatus;
import com.project.notification.models.NotificationLogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepo extends JpaRepository<NotificationLogs,Long> {
    boolean existsByBookingIdAndType(Long bookingId, String status);
}
