package com.keyur.queue_x.Repositories;

import com.keyur.queue_x.Entities.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord, Long> {
}
