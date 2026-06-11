package com.keyur.queue_x.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderDto {
    @NotNull(message = "amount cannot be null")
    @Positive(message = "amount must be greater than 0")
    private Double amount;

    @NotNull(message = "userId cannot be null")
    @Positive(message = "userId must be positive")
    private Long userId;

    @NotBlank(message = "idempotencyKey cannot be blank")
    @Size(min = 8, max = 64, message = "idempotencyKey must be between 8 and 64 characters")
    private String idempotencyKey;

    @NotNull(message = "productId cannot be null")
    private Long productId;

    @NotNull(message = "quantity cannot be null")
    @Positive(message = "quantity should be greater than 0")
    private Integer quantity;
}
