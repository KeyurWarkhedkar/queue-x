package com.keyur.queue_x.Entities;

import com.keyur.queue_x.Enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders",
uniqueConstraints = {
        @UniqueConstraint(columnNames = "idempotencyKey")
})
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    // PLACED, PROCESSING, COMPLETED, FAILED

    private String idempotencyKey;

    private Long productId;

    private Integer quantity;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
