package com.ccp.WorkBridge.payment;

import com.ccp.WorkBridge.enums.PaymentProviderType;
import com.ccp.WorkBridge.enums.PaymentStatus;
import com.ccp.WorkBridge.order.Order;
import com.ccp.WorkBridge.shared.BaseEntity;
import com.ccp.WorkBridge.subscription.Subscription;
import com.ccp.WorkBridge.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "payments")
public class Payment extends BaseEntity {
    @Column(name = "external_payment_id", unique = true)
    private String externalPaymentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentProviderType provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    private Instant paidAt;
    private String failureReason;

}
