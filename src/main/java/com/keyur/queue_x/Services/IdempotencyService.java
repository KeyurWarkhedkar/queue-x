package com.keyur.queue_x.Services;

import com.keyur.queue_x.Entities.IdempotencyRecord;
import com.keyur.queue_x.Repositories.IdempotencyRecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class IdempotencyService {

    private final IdempotencyRecordRepository idempotencyRecordRepository;

    @Transactional
    public boolean saveRecord(String messageId, Long orderId) {
        try {
            IdempotencyRecord record = new IdempotencyRecord();
            record.setMessageId(messageId);
            record.setOrderId(orderId);
            record.setCreatedAt(LocalDateTime.now());
            idempotencyRecordRepository.save(record);
        } catch (DataIntegrityViolationException e) {
            return false;
        }
        return true;
    }
}

