package com.ccp.WorkBridge.order.dto;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

public record CreateOrderRequest(
        String title,
        BigDecimal price,
        String description,
        Instant deadline,
        Set<OrderSkillRequest> skills
) {

    public record OrderSkillRequest(
            Long skillId,
            Integer requiredLevel
    ) {}
}
