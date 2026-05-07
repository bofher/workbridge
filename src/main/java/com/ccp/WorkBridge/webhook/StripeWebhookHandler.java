package com.ccp.WorkBridge.webhook;

import com.ccp.WorkBridge.payment.service.OrderPaymentService;
import com.ccp.WorkBridge.shared.exceptions.WebHookException;
import com.ccp.WorkBridge.user.service.connect.StripeConnectService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Account;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeWebhookHandler {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    private final OrderPaymentService paymentService;
    private final StripeConnectService stripeConnectService;

    public void handle(String payload, String sigHeader) {
        Event event = constructEvent(payload, sigHeader);
        log.info("Received Stripe webhook event: {}", event.getType());

        switch (event.getType()) {
            case "payment_intent.succeeded" -> handleSucceeded(event);
            case "payment_intent.amount_capturable_updated" -> handleAmountCapturableUpdated(event);
            case "payment_intent.payment_failed" -> handleFailed(event);
            case "payment_intent.canceled" -> handleCanceled(event);
            case "account.updated" -> handleAccountUpdated(event);
            default -> log.debug("Unhandled event type: {}", event.getType());
        }
    }

    private void handleSucceeded(Event event) {
        PaymentIntent intent = extractPaymentIntent(event);
        log.info("Payment intent succeeded: {}", intent.getId());

        if ("requires_capture".equals(intent.getStatus())) {
            paymentService.capturePayment(intent.getId());
        } else if ("succeeded".equals(intent.getStatus())) {
            paymentService.markAsPaid(intent.getId());
        }
    }

    private void handleAmountCapturableUpdated(Event event) {
        PaymentIntent intent = extractPaymentIntent(event);
        log.info("Payment intent amount capturable: {}", intent.getId());
        
        if (intent.getAmountCapturable() > 0) {
            paymentService.capturePayment(intent.getId());
        }
    }

    private void handleFailed(Event event) {
        PaymentIntent intent = extractPaymentIntent(event);
        log.warn("Payment intent failed: {}", intent.getId());
        
        paymentService.markAsFailed(
                intent.getId(),
                safeFailureReason(intent)
        );
    }

    private void handleCanceled(Event event) {
        PaymentIntent intent = extractPaymentIntent(event);
        log.warn("Payment intent canceled: {}", intent.getId());
        
        paymentService.cancelPayment(intent.getId());
    }

    private void handleAccountUpdated(Event event) {
        Account account = extractAccount(event);
        log.info("Account updated: {}", account.getId());
        
        stripeConnectService.updateAccountFromWebhook(account);
    }

    // ---- HELPERS ----

    private Event constructEvent(String payload, String sigHeader) {
        try {
            return Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            throw new WebHookException("Invalid Stripe signature: " + e.getMessage());
        }
    }

    private PaymentIntent extractPaymentIntent(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        return (PaymentIntent) deserializer.getObject()
                .orElseThrow(() -> new WebHookException(
                        "Cannot deserialize PaymentIntent for event: " + event.getId()
                ));
    }

    private Account extractAccount(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        return (Account) deserializer.getObject()
                .orElseThrow(() -> new WebHookException(
                        "Cannot deserialize Account for event: " + event.getId()
                ));
    }

    private String safeFailureReason(PaymentIntent intent) {
        return intent.getLastPaymentError() != null
                ? intent.getLastPaymentError().getMessage()
                : "Stripe payment failed";
    }
}