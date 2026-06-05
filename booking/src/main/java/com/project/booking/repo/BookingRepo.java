package com.project.booking.repo;

import com.project.booking.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepo extends JpaRepository<Booking,Long> {

    List<Booking> findByUserId(Long userId);
    List<Booking> findByRoomId(Long roomId);
}
