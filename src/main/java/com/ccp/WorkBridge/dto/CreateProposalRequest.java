package com.ccp.WorkBridge.dto;

public record CreateProposalRequest(
        Long orderId,
        String message
) {
}
