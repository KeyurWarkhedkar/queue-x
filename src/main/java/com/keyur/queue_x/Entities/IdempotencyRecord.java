package com.keyur.queue_x.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "idempotency_records",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "messageId")
        })
@Getter
@Setter
public class IdempotencyRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String messageId;

    private Long orderId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}


