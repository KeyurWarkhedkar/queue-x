package com.keyur.queue_x.Repositories;

import com.keyur.queue_x.Entities.Order;
import com.keyur.queue_x.Enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByIdempotencyKey(String idempotencyKey);

    @Modifying
    @Query("UPDATE Order o SET o.status = :newStatus " +
    "WHERE o.id = :id AND o.status = :oldStatus")
    int updateOrderStatus(@Param("id") Long id,
                          @Param("oldStatus") OrderStatus oldStatus,
                          @Param("newStatus") OrderStatus newStatus);
}
