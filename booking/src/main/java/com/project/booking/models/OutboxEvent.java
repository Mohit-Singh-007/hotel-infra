package com.project.booking.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String topic;           // booking.confirmed

    @Column(nullable = false)
    private String aggregateId;     // bookingId

    @Column(nullable = false)
    private String aggregateType;   // BOOKING

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;         // JSON of BookingEvent

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;    // PENDING, SENT, FAILED

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime sentAt;

    private int retryCount;
}
