package com.project.notification.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long bookingId;
    private Long userId;
    private String type;   // BOOKING_CREATED, BOOKING_CONFIRMED, BOOKING_CANCELLED
    private String message;

    @CreationTimestamp
    private LocalDateTime sentAt;
}