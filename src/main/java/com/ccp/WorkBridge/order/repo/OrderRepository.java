package com.ccp.WorkBridge.order.repo;

import com.ccp.WorkBridge.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Order getOrderById(Long id);
}
