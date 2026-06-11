package com.keyur.queue_x.Entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dead_letter_orders")
public class DeadLetterOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private String reason;

    private String lastError;

    private Integer retryCount;

    private LocalDateTime failedAt;
}
