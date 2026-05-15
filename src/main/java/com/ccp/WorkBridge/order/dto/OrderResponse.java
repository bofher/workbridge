package com.ccp.WorkBridge.order.dto;

import com.ccp.WorkBridge.enums.OrderStatus;
import com.ccp.WorkBridge.order.Order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record OrderResponse(
        Long id,
        String title,
        Long customerId,
        Long freelancerId,
        OrderStatus status,
        BigDecimal price,
        String description,
        Instant deadline,
        //TODO: return skillLevels
        List<SkillLevelResponse> skills
) {
    public static OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getTitle(),
                order.getCustomer().getId(),
                order.getFreelancer() != null ? order.getFreelancer().getId() : null,
                order.getStatus(),
                order.getPrice(),
                order.getDescription(),
                order.getDeadline(),
                order.getOrderSkills().stream()
                        .map(os -> new SkillLevelResponse(
                                os.getSkill().getSkillName(),
                                os.getRequiredLevel()
                        ))
                        .toList()
        );
    }
    public record SkillLevelResponse(
            String name,
            Integer requiredLevel
    ) {}
}
