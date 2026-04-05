package com.ccp.WorkBridge.dto;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

public record CreateOrderRequest(
        BigDecimal price,
        String description,
        Instant deadline,
        Set<Long> skillIds
) {
}
