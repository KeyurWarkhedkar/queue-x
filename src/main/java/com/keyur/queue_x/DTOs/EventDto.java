package com.keyur.queue_x.DTOs;

import com.keyur.queue_x.Enums.EventType;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class EventDto {

    private String eventId;        // unique message id (idempotency key)
    private EventType eventType;      // ORDER_CREATED, PAYMENT_SUCCESS, etc.
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private double amount;
    private Long userId;
    private String createdAt;
}
