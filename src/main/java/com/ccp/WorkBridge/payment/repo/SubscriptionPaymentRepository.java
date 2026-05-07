package com.ccp.WorkBridge.payment.repo;

import com.ccp.WorkBridge.payment.model.SubscriptionPayment;
import com.ccp.WorkBridge.subscription.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionPaymentRepository extends JpaRepository<SubscriptionPayment, Long> {
    Optional<SubscriptionPayment> findByExternalPaymentId(String externalPaymentId);

}
