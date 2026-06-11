package com.keyur.queue_x.Entities;

import com.keyur.queue_x.Enums.StepStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_processing_log")
public class OrderProcessingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private String step;
    // PAYMENT, INVENTORY, NOTIFICATION

    @Enumerated(EnumType.STRING)
    private StepStatus status;
    // SUCCESS, FAILED

    private String errorMessage;

    private Integer attemptNo;

    private LocalDateTime createdAt;
}
