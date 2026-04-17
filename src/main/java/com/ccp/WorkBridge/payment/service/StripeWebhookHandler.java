package com.ccp.WorkBridge.payment.service;

import com.ccp.WorkBridge.shared.exceptions.WebHookException;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripeWebhookHandler {
    @Value("${stripe.webhook.secret}")
    private String endpointSecret;
    private final PaymentService paymentService;

    public void handle(String payload, String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            switch (event.getType()) {
                case "payment_intent.succeeded" -> handleSucceeded(event);
                case "payment_intent.payment_failed" -> handleFailed(event);
            }
        } catch (SignatureVerificationException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleSucceeded(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();

        if (deserializer.getObject().isPresent()) {
            PaymentIntent paymentIntent = (PaymentIntent) deserializer.getObject().get();
            String externalPaymentId = paymentIntent.getId();
            paymentService.markAsPaid(externalPaymentId);
        } else {
            String eventApiVersion = event.getApiVersion();
            String libApiVersion = Stripe.API_VERSION;

            throw new WebHookException(
                    "Cannot deserialize PaymentIntent from event. " +
                            "Event API version: " + eventApiVersion +
                            ", Library API version: " + libApiVersion +
                            ". Consider upgrading stripe-java or changing webhook API version."
            );
        }
    }

    private void handleFailed(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject()
                .orElseThrow(() -> new WebHookException("Cannot deserialize event object"));

        String externalPaymentId = paymentIntent.getId();
        paymentService.markAsFailed(externalPaymentId, "Stripe failed");
    }
}
