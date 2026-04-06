package com.ccp.WorkBridge.models;

import com.ccp.WorkBridge.enums.ProposalStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

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