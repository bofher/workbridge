package com.ccp.WorkBridge.payment.model;

import com.ccp.WorkBridge.payment.model.enums.PaymentProviderType;
import com.ccp.WorkBridge.payment.model.enums.PaymentStatus;
import com.ccp.WorkBridge.shared.BaseEntity;
import com.ccp.WorkBridge.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class PaymentBase extends BaseEntity {

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

    private Instant paidAt;

    private String failureReason;
}