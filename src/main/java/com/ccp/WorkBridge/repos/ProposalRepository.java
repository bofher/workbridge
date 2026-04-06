package com.ccp.WorkBridge.repos;

import com.ccp.WorkBridge.models.Order;
import com.ccp.WorkBridge.models.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    List<Proposal> findAllByOrder(Order order);
}
