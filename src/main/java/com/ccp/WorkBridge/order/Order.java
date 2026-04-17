package com.ccp.WorkBridge.order;

import com.ccp.WorkBridge.enums.OrderStatus;
import com.ccp.WorkBridge.enums.OrderType;
import com.ccp.WorkBridge.message.Message;
import com.ccp.WorkBridge.user.User;
import com.ccp.WorkBridge.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "orders")
public class Order extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne
    @JoinColumn(name = "freelancer_id", nullable = true)
    private User freelancer;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private OrderType orderType;
    private String description;
    private Instant deadline;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderSkill> orderSkills = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderReview> reviews = new ArrayList<>();
}


