package com.ccp.WorkBridge.user;

import com.ccp.WorkBridge.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "stripe_connect_accounts")
public class StripeConnectAccount extends BaseEntity {

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, unique = true)
    private String stripeAccountId;

    @Column(nullable = false)
    private Boolean chargesEnabled = false;

    @Column(nullable = false)
    private Boolean payoutsEnabled = false;

    @Column(nullable = false)
    private Boolean detailsSubmitted = false;

    @Column(nullable = false)
    private Boolean requirementsDue = false;

    public boolean isReady() {
        return Boolean.TRUE.equals(chargesEnabled)
                && Boolean.TRUE.equals(payoutsEnabled)
                && Boolean.FALSE.equals(requirementsDue);
    }
}

