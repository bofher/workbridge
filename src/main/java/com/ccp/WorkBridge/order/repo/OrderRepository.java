package com.ccp.WorkBridge.order.repo;

import com.ccp.WorkBridge.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Order getOrderById(Long id);
}
