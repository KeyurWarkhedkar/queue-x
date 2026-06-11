package com.keyur.queue_x.Repositories;

import com.keyur.queue_x.Entities.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    @Query(value = """
            SELECT * FROM outbox_event 
            WHERE status = 'PENDING'
            AND event_type = 'ORDER_CREATED'
            ORDER BY created_at ASC 
            LIMIT 10
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<OutboxEvent> findPendingEventsForUpdate();
}
