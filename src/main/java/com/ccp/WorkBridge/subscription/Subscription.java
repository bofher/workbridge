package com.ccp.WorkBridge.subscription;

import com.ccp.WorkBridge.user.User;
import com.ccp.WorkBridge.shared.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subscriptions")
public class Subscription extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Instant startDate;
    private Instant endDate;

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlan plan;
    private String externalSubscriptionId; // for usage in payment systems

    public enum SubscriptionStatus {
        ACTIVE,
        CANCELED,
        EXPIRED,
        PAST_DUE
    }
}
