package com.ccp.WorkBridge.payment.model;

import com.ccp.WorkBridge.order.Order;
import com.ccp.WorkBridge.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderPayment extends PaymentBase {

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "stripe_transfer_id")
    private String stripeTransferId;

    @ManyToOne
    @JoinColumn(name = "freelancer_id", nullable = false)
    private User freelancer;
}

