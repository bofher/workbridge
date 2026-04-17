package com.ccp.WorkBridge.order.service;

import com.ccp.WorkBridge.dto.CreateOrderRequest;
import com.ccp.WorkBridge.enums.OrderStatus;
import com.ccp.WorkBridge.enums.OrderType;
import com.ccp.WorkBridge.order.Order;
import com.ccp.WorkBridge.order.OrderSkill;
import com.ccp.WorkBridge.shared.exceptions.DataNotFoundException;
import com.ccp.WorkBridge.order.repo.OrderRepository;
import com.ccp.WorkBridge.skill.repo.SkillRepository;
import com.ccp.WorkBridge.skill.Skill;
import com.ccp.WorkBridge.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        //TODO: make skill level in orderSKill
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

}
