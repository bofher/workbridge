package com.ccp.WorkBridge.subscription;

import com.ccp.WorkBridge.shared.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subscription_plans")
public class SubscriptionPlan extends BaseEntity {

    private String name;
    private Double price;
    private String currency;
    private Integer durationDays;
    private String externalPriceId; // for usage in payment systems
    private Double boostCoefficient;
}
