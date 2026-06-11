package com.keyur.queue_x.Services;

import com.keyur.queue_x.DTOs.EventDto;
import com.keyur.queue_x.Entities.Order;
import com.keyur.queue_x.Enums.OrderStatus;
import com.keyur.queue_x.Repositories.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class OutOfStockConsumer {

    private final RedisMessageQueue messageQueue;
    private final OrderRepository orderRepository;

    @Scheduled(fixedDelay = 100)
    public void consumeOutOfStockEvents() {
        EventDto event = messageQueue.consume(QueueConstants.outOfStock);

        if(event == null) {
            return;
        }

        processEvent(event);
    }

    @Transactional
    public void processEvent(EventDto event) {

        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found: " + event.getOrderId()));

        /*
         * Idempotent:
         * if already FAILED, do nothing.
         */
        if(order.getStatus() == OrderStatus.FAILED) {
            return;
        }

        /*
         * Don't overwrite terminal states.
         */
        if(order.getStatus() == OrderStatus.COMPLETED) {
            log.warn(
                    "Received OUT_OF_STOCK for completed order={}",
                    order.getId()
            );
            return;
        }

        order.setStatus(OrderStatus.FAILED);

        orderRepository.save(order);

        log.info(
                "Order {} marked FAILED due to insufficient inventory",
                order.getId()
        );
    }
}
