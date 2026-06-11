package com.keyur.queue_x.Services;

import com.keyur.queue_x.DTOs.CreateOrderDto;
import com.keyur.queue_x.DTOs.OrderResponseDto;
import com.keyur.queue_x.Entities.Order;
import com.keyur.queue_x.Entities.OutboxEvent;
import com.keyur.queue_x.Enums.EventType;
import com.keyur.queue_x.Enums.OrderStatus;
import com.keyur.queue_x.Enums.OutboxStatus;
import com.keyur.queue_x.Repositories.OrderRepository;
import com.keyur.queue_x.Repositories.OutboxEventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    // Fields
    private final OutboxEventRepository outboxEventRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public OrderResponseDto createOrder(CreateOrderDto createOrderDto) {
        // Check if we have the order already created with idempotency key
        String idempotencyKey = createOrderDto.getIdempotencyKey();

        Optional<Order> optionalOrder = orderRepository.findByIdempotencyKey(idempotencyKey);
        if(optionalOrder.isPresent()) {
            Order existingOrder = optionalOrder.get();
            return buildOrderResponse(existingOrder);
        }

        // Create order if not idempotent
        Order newOrder = new Order();
        newOrder.setUserId(createOrderDto.getUserId());
        newOrder.setStatus(OrderStatus.PLACED);
        newOrder.setAmount(createOrderDto.getAmount());
        newOrder.setCreatedAt(LocalDateTime.now());
        newOrder.setProductId(createOrderDto.getProductId());
        newOrder.setQuantity(createOrderDto.getQuantity());
        newOrder.setIdempotencyKey(createOrderDto.getIdempotencyKey());
        try {
            orderRepository.save(newOrder);
        } catch(DataIntegrityViolationException e) {
            // Race condition hit.
            return buildOrderResponse(orderRepository.findByIdempotencyKey(idempotencyKey)
                    .orElseThrow(() -> new RuntimeException("Order could not be created. Some error occurred.")));
        }

        // Write to Outbox table for redis publishing
        writeToOutbox(newOrder);

        return buildOrderResponse(newOrder);
    }

    private void writeToOutbox(Order newOrder) {
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setAmount(newOrder.getAmount());
        outboxEvent.setIdempotencyKey(UUID.randomUUID().toString());
        outboxEvent.setEventType(EventType.ORDER_CREATED);
        outboxEvent.setStatus(OutboxStatus.PENDING);
        outboxEvent.setOrderId(newOrder.getId());
        outboxEvent.setRetryCount(0);
        outboxEvent.setProductId(newOrder.getProductId());
        outboxEvent.setQuantity(newOrder.getQuantity());
        outboxEvent.setUserId(newOrder.getUserId());
        outboxEvent.setCreatedAt(LocalDateTime.now());
        outboxEventRepository.save(outboxEvent);
    }

    private OrderResponseDto buildOrderResponse(Order existingOrder) {
        OrderResponseDto responseDto = new OrderResponseDto();
        responseDto.setOrderId(existingOrder.getId());
        responseDto.setStatus(existingOrder.getStatus());
        responseDto.setMessage("Order under process!");
        return responseDto;
    }
}
