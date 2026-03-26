package com.ccp.WorkBridge.models;

import com.ccp.WorkBridge.enums.PaymentProviderType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    private Double amount;

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
    
    public enum PaymentStatus {
        CREATED,
        REQUIRES_PAYMENT,
        SUCCEEDED,
        FAILED,
        CANCELED
    }
}
