package com.project.booking.repo;

import com.project.booking.models.OutboxEvent;
import com.project.booking.models.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepo extends JpaRepository<OutboxEvent,String> {

    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
