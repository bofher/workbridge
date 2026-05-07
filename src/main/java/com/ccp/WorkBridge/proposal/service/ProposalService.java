package com.ccp.WorkBridge.proposal.service;


import com.ccp.WorkBridge.dto.CreateProposalRequest;
import com.ccp.WorkBridge.enums.OrderStatus;
import com.ccp.WorkBridge.enums.ProposalStatus;
import com.ccp.WorkBridge.order.Order;
import com.ccp.WorkBridge.payment.service.OrderPaymentService;
import com.ccp.WorkBridge.proposal.Proposal;
import com.ccp.WorkBridge.shared.exceptions.DataNotFoundException;
import com.ccp.WorkBridge.user.User;
import com.ccp.WorkBridge.order.repo.OrderRepository;
import com.ccp.WorkBridge.proposal.repo.ProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProposalService {
    private final ProposalRepository proposalRepository;
    private final OrderRepository orderRepository;
    private final OrderPaymentService orderPaymentService;

    public Proposal createProposal(CreateProposalRequest request, User freelancer) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getFreelancer() != null) {
            throw new IllegalStateException("Order already has freelancer");
        }
        Proposal proposal = new Proposal();
        proposal.setOrder(order);
        proposal.setFreelancer(freelancer);
        proposal.setMessage(request.message());
        proposal.setStatus(ProposalStatus.PENDING);

        return proposalRepository.save(proposal);
    }

    public Long acceptProposal(Long proposalId, User customer) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new DataNotFoundException("Proposal not found"));

        Order order = proposal.getOrder();

        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalStateException("Not your order");
        }
        if (order.getFreelancer() != null) {
            throw new IllegalStateException("Freelancer already selected");
        }
        
        order.setFreelancer(proposal.getFreelancer());
        order.setStatus(OrderStatus.IN_PROGRESS);

        proposal.setStatus(ProposalStatus.ACCEPTED);

        List<Proposal> others = proposalRepository.findAllByOrder(order);
        others.stream()
                .filter(p -> !p.getId().equals(proposalId))
                .forEach(p -> p.setStatus(ProposalStatus.REJECTED));

        orderRepository.save(order);
        proposalRepository.saveAll(others);

        return orderPaymentService.createPayment(customer, order.getPrice(), "USD", order).getId();
    }

    public List<Proposal> getByOrder(Long orderId) {
        return proposalRepository.findAllByOrder(orderRepository.getOrderById(orderId));
    }
}