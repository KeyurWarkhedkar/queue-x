package com.keyur.queue_x.Services;

import com.keyur.queue_x.DTOs.EventDto;
import com.keyur.queue_x.Enums.OrderStatus;
import com.keyur.queue_x.Repositories.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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

    @Scheduled(fixedDelay = 100)
    @Transactional
    public void consumePaymentSuccess() {
        EventDto event = messageQueue.consume(QueueConstants.paymentSuccessQueue);
        if(event == null) return;
        processPaymentSuccess(event);
    }

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void consumePaymentFailed() {
        EventDto event = messageQueue.consume(QueueConstants.paymentFailedForOrderApiQueue);
        if(event == null) return;
        processPaymentFailed(event);
    }


    public void processPaymentSuccess(EventDto event) {
        // Each event is consumed on the scheduler's own thread — set MDC fresh per event,
        // clear after, since this thread is reused across unrelated orders on every poll cycle.
        try {
            MDC.put("orderId", String.valueOf(event.getOrderId()));
            log.info("sagaStep=PAYMENT_SUCCESS_CONSUMED eventId={} orderId={}", event.getEventId(), event.getOrderId());

            if(!idempotencyService.saveRecord(event.getEventId(), event.getOrderId())) {
                log.info("sagaStep=PAYMENT_SUCCESS_SKIPPED reason=ALREADY_PROCESSED eventId={}", event.getEventId());
                return;
            }

            int affected = orderRepository.updateOrderStatus(
                    event.getOrderId(),
                    OrderStatus.PLACED,
                    OrderStatus.COMPLETED
            );

            if(affected == 0) {
                log.warn("sagaStep=PAYMENT_SUCCESS_SKIPPED reason=NOT_IN_PLACED_STATUS orderId={}", event.getOrderId());
            }

            log.info("sagaStep=ORDER_COMPLETED orderId={}", event.getOrderId());
        } finally {
            MDC.clear();
        }
    }


    public void processPaymentFailed(EventDto event) {
        try {
            MDC.put("orderId", String.valueOf(event.getOrderId()));
            log.info("sagaStep=PAYMENT_FAILED_CONSUMED eventId={} orderId={}", event.getEventId(), event.getOrderId());

            if(!idempotencyService.saveRecord(event.getEventId(), event.getOrderId())) {
                log.info("sagaStep=PAYMENT_FAILED_SKIPPED reason=ALREADY_PROCESSED eventId={}", event.getEventId());
                return;
            }

            int affected = orderRepository.updateOrderStatus(
                    event.getOrderId(),
                    OrderStatus.PLACED,
                    OrderStatus.FAILED
            );

            if(affected == 0) {
                log.warn("sagaStep=PAYMENT_FAILED_SKIPPED reason=NOT_IN_PLACED_STATUS orderId={}", event.getOrderId());
            }

            log.info("sagaStep=ORDER_FAILED orderId={}", event.getOrderId());
        } finally {
            MDC.clear();
        }
    }
}