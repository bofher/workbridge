package com.ccp.WorkBridge.repos;

import com.ccp.WorkBridge.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
