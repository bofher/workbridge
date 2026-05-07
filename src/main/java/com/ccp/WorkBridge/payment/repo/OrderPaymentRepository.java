package com.ccp.WorkBridge.payment.repo;

import com.ccp.WorkBridge.payment.model.OrderPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderPaymentRepository extends JpaRepository<OrderPayment, Long> {
    Optional<OrderPayment> findByExternalPaymentId(String externalPaymentId);

    Optional<OrderPayment> findByOrderId(Long orderId);
}
