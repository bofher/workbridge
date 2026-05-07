package com.ccp.WorkBridge.payment.model;

import com.ccp.WorkBridge.subscription.Subscription;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subscription_payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPayment extends PaymentBase {

    @ManyToOne
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;
}

