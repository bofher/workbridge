package com.ccp.WorkBridge.user.controller;

import com.ccp.WorkBridge.dto.CustomUserDetails;
import com.ccp.WorkBridge.user.StripeConnectAccount;
import com.ccp.WorkBridge.user.service.connect.StripeConnectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for managing Stripe Connect account operations.
 * Handles onboarding, status tracking, and account synchronization.
 */
@RestController
@RequestMapping("/api/stripe/connect")
@RequiredArgsConstructor
public class StripeConnectController {

    private final StripeConnectService stripeConnectService;

    /**
     * Creates an onboarding link for the authenticated user's Stripe Connect account.
     * 
     * @param userDetails authenticated user details extracted from JWT token
     * @return response containing the onboarding URL
     * @throws IllegalStateException if account is already fully configured
     */
    @PostMapping("/onboarding")
    public ResponseEntity<Map<String, String>> createOnboardingLink(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        String onboardingUrl = stripeConnectService.createOnboardingLink(userDetails.user());
        
        return ResponseEntity.ok(Map.of("onboardingUrl", onboardingUrl));
    }

    /**
     * Retrieves the current status of the user's Stripe Connect account.
     * 
     * @param userDetails authenticated user details extracted from JWT token
     * @return account status with all relevant flags and capabilities
     * @throws IllegalStateException if account not found for user
     */
    @GetMapping("/status")
    public ResponseEntity<StripeConnectAccountDto> getStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        StripeConnectAccount account = stripeConnectService.getByUser(userDetails.user());
        
        return ResponseEntity.ok(mapToDto(account));
    }

    /**
     * Checks if the user's Stripe Connect account is fully ready for transactions.
     * An account is considered ready when charges and payouts are enabled 
     * and all requirements are satisfied.
     * 
     * @param userDetails authenticated user details extracted from JWT token
     * @return boolean flag indicating account readiness
     */
    @GetMapping("/ready")
    public ResponseEntity<Map<String, Boolean>> isReady(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        boolean isReady = stripeConnectService.isAccountReady(userDetails.user());
        
        return ResponseEntity.ok(Map.of("isReady", isReady));
    }

    /**
     * Retrieves the list of pending requirements for the user's Stripe Connect account.
     * Requirements indicate what additional information must be provided before 
     * the account can be fully activated.
     * 
     * @param userDetails authenticated user details extracted from JWT token
     * @return list of requirement field names that need to be completed
     * @throws IllegalStateException if account not found for user
     */
    @GetMapping("/requirements")
    public ResponseEntity<Map<String, List<String>>> getRequirements(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        List<String> requirements = stripeConnectService.getRequirements(userDetails.user());
        
        return ResponseEntity.ok(Map.of("requirements", requirements));
    }

    /**
     * Synchronizes the local account data with the current state in Stripe.
     * Fetches latest information about charges enabled, payouts, verification status, etc.
     * Called typically after user completes onboarding in Stripe dashboard.
     * 
     * @param userDetails authenticated user details extracted from JWT token
     * @return updated account status with synchronized data
     * @throws IllegalStateException if account not found or Stripe API error occurs
     */
    @PostMapping("/sync")
    public ResponseEntity<StripeConnectAccountDto> syncStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        StripeConnectAccount account = stripeConnectService.syncAccountStatus(userDetails.user());
        
        return ResponseEntity.ok(mapToDto(account));
    }

    /**
     * Handles redirect when user clicks "Refresh" during Stripe onboarding.
     * Indicates that user has not completed onboarding but can return later.
     * 
     * @return response with refresh status message
     */
    @GetMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshOnboarding() {
        return ResponseEntity.ok(Map.of(
            "message", "Refresh successful. Please continue with onboarding.",
            "status", "refresh"
        ));
    }

    /**
     * Handles redirect when user successfully completes Stripe Connect onboarding.
     * Frontend should automatically call /sync endpoint after receiving this response.
     * 
     * @return response with completion status message
     */
    @GetMapping("/return")
    public ResponseEntity<Map<String, String>> returnFromOnboarding() {
        return ResponseEntity.ok(Map.of(
            "message", "Onboarding completed successfully.",
            "status", "completed"
        ));
    }

    /**
     * Converts StripeConnectAccount entity to DTO for API response.
     * 
     * @param account the entity to convert
     * @return DTO with account information
     */
    private StripeConnectAccountDto mapToDto(StripeConnectAccount account) {
        return new StripeConnectAccountDto(
            account.getStripeAccountId(),
            account.getChargesEnabled(),
            account.getPayoutsEnabled(),
            account.getDetailsSubmitted(),
            account.getRequirementsDue(),
            account.isReady()
        );
    }

    /**
     * Data Transfer Object representing Stripe Connect account status.
     * 
     * @param stripeAccountId unique Stripe account identifier
     * @param chargesEnabled whether account can accept charges
     * @param payoutsEnabled whether account can receive payouts
     * @param detailsSubmitted whether legal/business details have been submitted
     * @param requirementsDue whether additional information is pending
     * @param isReady whether account is fully operational (all capabilities enabled)
     */
    public record StripeConnectAccountDto(
        String stripeAccountId,
        Boolean chargesEnabled,
        Boolean payoutsEnabled,
        Boolean detailsSubmitted,
        Boolean requirementsDue,
        Boolean isReady
    ) {}
}