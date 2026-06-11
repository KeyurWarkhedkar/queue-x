package com.keyur.queue_x.Entities;

import com.keyur.queue_x.Enums.EventType;
import com.keyur.queue_x.Enums.OutboxStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_event")
@Getter
@Setter
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Lob
    private String payload; // JSON string

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    private Integer retryCount;

    private Long productId;

    private Integer quantity;

    private LocalDateTime createdAt;

    private String idempotencyKey;

    private Long userId;

    private double amount;
}
