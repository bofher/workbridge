package com.ccp.WorkBridge.repos;

import com.ccp.WorkBridge.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Order getOrderById(Long id);
}
