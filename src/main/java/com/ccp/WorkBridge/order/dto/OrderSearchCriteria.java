package com.ccp.WorkBridge.order.dto;

import com.ccp.WorkBridge.enums.OrderStatus;
import com.ccp.WorkBridge.enums.OrderType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.ccp.WorkBridge.order.Order;

@Getter
@Setter
public class OrderSearchCriteria {

    /**
     * Field to search by description and title field in {@link Order}
     */
    private String query;

    private OrderStatus status;

    private OrderType orderType;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private Instant minDeadline;

    private Instant maxDeadline;

    private List<SkillFilter> skills;

    public record SkillFilter(
            Long skillId,
            Integer minLevel,
            Integer maxLevel
    ) {}
}
