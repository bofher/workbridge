package com.ccp.WorkBridge.proposal;

import com.ccp.WorkBridge.enums.ProposalStatus;
import com.ccp.WorkBridge.order.Order;
import com.ccp.WorkBridge.shared.BaseEntity;
import com.ccp.WorkBridge.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "proposals")
public class Proposal extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "freelancer_id", nullable = false)
    private User freelancer;

    private String message;

    @Enumerated(EnumType.STRING)
    private ProposalStatus status;
}