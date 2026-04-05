package com.ccp.WorkBridge.dto;

import com.ccp.WorkBridge.enums.OrderStatus;
import com.ccp.WorkBridge.models.Order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

public record OrderResponse(
        Long id,
        Long customerId,
        Long freelancerId,
        OrderStatus status,
        BigDecimal price,
        String description,
        Instant deadline,
        Set<String> skillNames
) {
    public static OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomer().getId(),
                order.getFreelancer() != null ? order.getFreelancer().getId() : null,
                order.getStatus(),
                order.getPrice(),
                order.getDescription(),
                order.getDeadline(),
                order.getOrderSkills().stream()
                        .map(os -> os.getSkill().getSkillName())
                        .collect(Collectors.toSet())
        );
    }
}
