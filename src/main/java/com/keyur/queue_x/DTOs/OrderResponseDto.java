package com.keyur.queue_x.DTOs;

import com.keyur.queue_x.Enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {
    public Long orderId;
    public OrderStatus status;
    public String message;
}
