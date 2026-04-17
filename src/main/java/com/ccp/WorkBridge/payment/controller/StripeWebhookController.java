package com.ccp.WorkBridge.payment.controller;

import com.ccp.WorkBridge.payment.service.StripeWebhookHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook/stripe")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final StripeWebhookHandler webhookHandler;

    @PostMapping
    public ResponseEntity<Void> handle(@RequestBody String payload,
                                       @RequestHeader("Stripe-Signature") String signature) {
        webhookHandler.handle(payload, signature);
        return ResponseEntity.ok().build();
    }
}
