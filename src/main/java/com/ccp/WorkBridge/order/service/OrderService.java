package com.ccp.WorkBridge.order.service;

import com.ccp.WorkBridge.dto.CreateOrderRequest;
import com.ccp.WorkBridge.enums.OrderStatus;
import com.ccp.WorkBridge.enums.OrderType;
import com.ccp.WorkBridge.order.Order;
import com.ccp.WorkBridge.order.OrderSkill;
import com.ccp.WorkBridge.payment.service.OrderPaymentService;
import com.ccp.WorkBridge.shared.exceptions.DataNotFoundException;
import com.ccp.WorkBridge.order.repo.OrderRepository;
import com.ccp.WorkBridge.skill.repo.SkillRepository;
import com.ccp.WorkBridge.skill.Skill;
import com.ccp.WorkBridge.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final SkillRepository skillRepository;
    private final OrderPaymentService orderPaymentService;

    public Order createOrder(CreateOrderRequest request, User currentUser) {
        Order order = new Order();
        order.setCustomer(currentUser);
        order.setFreelancer(null);
        order.setStatus(OrderStatus.CREATED);
        order.setPrice(request.price());
        order.setDescription(request.description());
        order.setDeadline(request.deadline());
        order.setOrderType(OrderType.REQUEST);

        Set<OrderSkill> skills = request.skills().stream()
                .map(skillRequest -> {
                    Skill skill = skillRepository.findById(skillRequest.skillId())
                            .orElseThrow(() -> new DataNotFoundException("skill not found"));

                    OrderSkill orderSkill = new OrderSkill();
                    orderSkill.setOrder(order);
                    orderSkill.setSkill(skill);
                    orderSkill.setRequiredLevel(skillRequest.requiredLevel());

                    return orderSkill;
                })
                .collect(Collectors.toSet());

        order.getOrderSkills().addAll(skills);
        return orderRepository.save(order);
    }

    @Transactional
    public void markOrderAsReady(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DataNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                "Order must be IN_PROGRESS to mark as ready. Current status: " + order.getStatus()
            );
        }

        orderPaymentService.confirmOrderReady(orderId);

        order.setStatus(OrderStatus.READY);
        orderRepository.save(order);

        log.info("Order {} marked as READY by freelancer", orderId);
    }

    @Transactional
    public void completeOrderAndExecutePayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DataNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.READY) {
            throw new IllegalStateException(
                "Order must be READY to complete. Current status: " + order.getStatus()
            );
        }
        orderPaymentService.completeOrderAndTransfer(orderId);

        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);

        log.info("Order {} completed and payment transferred to freelancer", orderId);
    }
}