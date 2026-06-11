package com.keyur.queue_x.Services;

import com.keyur.queue_x.DTOs.CreateOrderDto;
import com.keyur.queue_x.DTOs.OrderResponseDto;

public interface OrderService {
    public OrderResponseDto createOrder(CreateOrderDto createOrderDto);
}
