package com.keyur.queue_x.Services;

import com.keyur.queue_x.DTOs.EventDto;
import com.keyur.queue_x.Enums.OrderStatus;
import com.keyur.queue_x.Repositories.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class OrderStatusConsumer {

    private final MessageQueue messageQueue;
    private final OrderRepository orderRepository;
    private final IdempotencyService idempotencyService;

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void consumePaymentSuccess() {
        EventDto event = messageQueue.consume(QueueConstants.paymentSuccessQueue);
        if(event == null) return;
        processPaymentSuccess(event);
    }

    @Scheduled(fixedDelay = 100)
    @Transactional
    public void consumePaymentFailed() {
        EventDto event = messageQueue.consume(QueueConstants.paymentFailedForOrderApiQueue);
        if(event == null) return;
        processPaymentFailed(event);
    }


    public void processPaymentSuccess(EventDto event) {
        if(!idempotencyService.saveRecord(event.getEventId(), event.getOrderId())) {
            log.info("Already processed success eventId={}, skipping", event.getEventId());
            return;
        }

        int affected = orderRepository.updateOrderStatus(
                event.getOrderId(),
                OrderStatus.PLACED,
                OrderStatus.COMPLETED
        );

        if(affected == 0) {
            log.warn("Order {} not in PLACED status, skipping update", event.getOrderId());
        }

        log.info("Order {} marked COMPLETED", event.getOrderId());
    }


    public void processPaymentFailed(EventDto event) {
        if(!idempotencyService.saveRecord(event.getEventId(), event.getOrderId())) {
            log.info("Already processed failed eventId={}, skipping", event.getEventId());
            return;
        }

        int affected = orderRepository.updateOrderStatus(
                event.getOrderId(),
                OrderStatus.PLACED,
                OrderStatus.FAILED
        );

        if(affected == 0) {
            log.warn("Order {} not in PLACED status, skipping update", event.getOrderId());
        }

        log.info("Order {} marked FAILED", event.getOrderId());
    }
}
