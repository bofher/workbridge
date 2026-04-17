package com.ccp.WorkBridge.proposal.repo;

import com.ccp.WorkBridge.order.Order;
import com.ccp.WorkBridge.proposal.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    List<Proposal> findAllByOrder(Order order);
}
