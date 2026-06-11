package com.keyur.queue_x.Controllers;

import com.keyur.queue_x.DTOs.CreateOrderDto;
import com.keyur.queue_x.DTOs.OrderResponseDto;
import com.keyur.queue_x.Services.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/order")
public class OrderController {
    // Fields
    private final OrderService orderService;

    @PostMapping(value="/post")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody CreateOrderDto createOrderDto) {
        return new ResponseEntity<>(orderService.createOrder(createOrderDto), HttpStatus.CREATED);
    }
}
