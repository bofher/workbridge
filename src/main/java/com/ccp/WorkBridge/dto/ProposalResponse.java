package com.ccp.WorkBridge.dto;

import com.ccp.WorkBridge.enums.ProposalStatus;
import com.ccp.WorkBridge.models.Proposal;

public record ProposalResponse(
        Long id,
        Long orderId,
        Long freelancerId,
        String message,
        ProposalStatus status
) {
    public static ProposalResponse from(Proposal proposal) {
        return new ProposalResponse(
                proposal.getId(),
                proposal.getOrder().getId(),
                proposal.getFreelancer().getId(),
                proposal.getMessage(),
                proposal.getStatus()
        );
    }
}
