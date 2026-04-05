package com.ccp.WorkBridge.services;

import com.ccp.WorkBridge.dto.CreateOrderRequest;
import com.ccp.WorkBridge.enums.OrderStatus;
import com.ccp.WorkBridge.enums.OrderType;
import com.ccp.WorkBridge.exceptions.DataNotFoundException;
import com.ccp.WorkBridge.models.Order;
import com.ccp.WorkBridge.models.OrderSkill;
import com.ccp.WorkBridge.models.Skill;
import com.ccp.WorkBridge.models.User;
import com.ccp.WorkBridge.repos.OrderRepository;
import com.ccp.WorkBridge.repos.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final SkillRepository skillRepository;

    public Order createOrder(CreateOrderRequest request, User currentUser) {
        Order order = new Order();
        order.setCustomer(currentUser);
        order.setFreelancer(null);
        order.setStatus(OrderStatus.CREATED);
        order.setPrice(request.price());
        order.setDescription(request.description());
        order.setDeadline(request.deadline());
        order.setOrderType(OrderType.REQUEST);

        Set<OrderSkill> skills = request.skillIds().stream()
                .map(skillId -> {
                    Skill skill = skillRepository.findById(skillId)
                            .orElseThrow(() -> new DataNotFoundException("skill not found"));
                    OrderSkill orderSkill = new OrderSkill();
                    orderSkill.setOrder(order);
                    orderSkill.setSkill(skill);
                    return orderSkill;
                })
                .collect(Collectors.toSet());

        order.getOrderSkills().addAll(skills);

        return orderRepository.save(order);
    }

    public Order respondToOrder(Long orderId, User freelancer) {
        Order order = orderRepository.getOrderById(orderId);

        if (order.getFreelancer() != null) {
            throw new IllegalStateException("Order already taken");
        }
        order.setFreelancer(freelancer);
        order.setStatus(OrderStatus.PENDING_PAYMENT);

        return orderRepository.save(order);
    }

}
