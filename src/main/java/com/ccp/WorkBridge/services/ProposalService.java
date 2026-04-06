package com.ccp.WorkBridge.services;


import com.ccp.WorkBridge.dto.CreateProposalRequest;
import com.ccp.WorkBridge.enums.OrderStatus;
import com.ccp.WorkBridge.enums.ProposalStatus;
import com.ccp.WorkBridge.models.Order;
import com.ccp.WorkBridge.models.Payment;
import com.ccp.WorkBridge.models.Proposal;
import com.ccp.WorkBridge.models.User;
import com.ccp.WorkBridge.repos.OrderRepository;
import com.ccp.WorkBridge.repos.PaymentRepository;
import com.ccp.WorkBridge.repos.ProposalRepository;
import com.ccp.WorkBridge.services.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProposalService {
    private final ProposalRepository proposalRepository;
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;

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

    public String acceptProposal(Long proposalId, User customer) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found"));

        Order order = proposal.getOrder();

        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalStateException("Not your order");
        }
        if (order.getFreelancer() != null) {
            throw new IllegalStateException("Freelancer already selected");
        }
        order.setFreelancer(proposal.getFreelancer());
        order.setStatus(OrderStatus.PENDING_PAYMENT);

        proposal.setStatus(ProposalStatus.ACCEPTED);

        List<Proposal> others = proposalRepository.findAllByOrder(order);
        others.stream()
                .filter(p -> !p.getId().equals(proposalId))
                .forEach(p -> p.setStatus(ProposalStatus.REJECTED));

        orderRepository.save(order);
        proposalRepository.saveAll(others);

        Payment payment = paymentService.createPayment(
                order.getCustomer(),
                order.getPrice(),
                "usd", //TODO: multicurrency
                order,
                null
        );
        return paymentService.processPayment(payment);
    }

    public List<Proposal> getByOrder(Long orderId) {
        return proposalRepository.findAllByOrder(orderRepository.getOrderById(orderId));
    }
}
