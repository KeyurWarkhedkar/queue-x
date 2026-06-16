package com.keyur.queue_x.Services;

import com.keyur.queue_x.DTOs.EventDto;
import com.keyur.queue_x.Entities.OutboxEvent;
import com.keyur.queue_x.Enums.OutboxStatus;
import com.keyur.queue_x.Repositories.OutboxEventRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Queue;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Component
public class OutboxPoller {
    // Fields
    private final OutboxEventRepository outboxEventRepository;
    private final MessageQueue messageQueue;
    private final QueueConstants qs;

    @Scheduled(fixedDelay = 1000) // runs every 5 seconds
    @Transactional
    public void pollAndPublish() {
        List<OutboxEvent> pendingEvents = outboxEventRepository
                .findPendingEventsForUpdate();

        for(OutboxEvent event : pendingEvents) {
            try {
                EventDto eventDto = buildEventDto(event);
                messageQueue.publish(QueueConstants.orderQueue, eventDto);
                log.info("Published event {} to {}", eventDto, qs.urlMap().get(QueueConstants.orderQueue));
                event.setStatus(OutboxStatus.PUBLISHED);
                outboxEventRepository.save(event);

            } catch(Exception e) {
                log.error("Failed to publish outbox event id={}, retryCount={}",
                        event.getId(), event.getRetryCount(), e);

                event.setRetryCount(event.getRetryCount() + 1);

                if(event.getRetryCount() >= 5) {
                    event.setStatus(OutboxStatus.FAILED);
                    log.error("Outbox event id={} marked FAILED after max retries", event.getId());
                }

                outboxEventRepository.save(event);
            }
        }
    }

    private EventDto buildEventDto(OutboxEvent event) {
        EventDto eventDto = new EventDto();
        eventDto.setEventId(event.getIdempotencyKey());
        eventDto.setOrderId(event.getOrderId());
        eventDto.setEventType(event.getEventType());
        eventDto.setAmount(event.getAmount());
        eventDto.setProductId(event.getProductId());
        eventDto.setQuantity(event.getQuantity());
        eventDto.setUserId(event.getUserId());
        eventDto.setCreatedAt(event.getCreatedAt().toString());
        return eventDto;
    }
}
