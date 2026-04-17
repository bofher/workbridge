package com.ccp.WorkBridge.proposal.controller;

import com.ccp.WorkBridge.dto.CreateProposalRequest;
import com.ccp.WorkBridge.dto.CustomUserDetails;
import com.ccp.WorkBridge.dto.ProposalResponse;
import com.ccp.WorkBridge.proposal.service.ProposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proposals")
@RequiredArgsConstructor
public class ProposalController {

    private final ProposalService proposalService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ProposalResponse create(@RequestBody CreateProposalRequest request,
                                   @AuthenticationPrincipal CustomUserDetails currentUser) {
        return ProposalResponse.from(
                proposalService.createProposal(request, currentUser.user())
        );
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/{id}/accept")
    public String accept(@PathVariable Long id,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        return proposalService.acceptProposal(id, userDetails.user());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/order/{orderId}")
    public List<ProposalResponse> getByOrder(@PathVariable Long orderId) {
        return proposalService.getByOrder(orderId)
                .stream()
                .map(ProposalResponse::from)
                .toList();
    }
}
