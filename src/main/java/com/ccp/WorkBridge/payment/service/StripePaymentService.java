package com.ccp.WorkBridge.payment.service;

import com.ccp.WorkBridge.shared.exceptions.PaymentFailedException;
import com.ccp.WorkBridge.payment.Payment;
import com.ccp.WorkBridge.payment.service.interfaces.PaymentProviderService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class StripePaymentService implements PaymentProviderService {

    @Override
    public String createPaymentIntent(Payment payment) throws PaymentFailedException {
        if (payment.getExternalPaymentId() != null) {
            throw new IllegalStateException("Payment already has external id");
        }
        try {
            long amountInCents = payment.getAmount().multiply(BigDecimal.valueOf(100)).longValue();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(payment.getCurrency().toLowerCase())
                    .putMetadata("paymentId", payment.getId().toString())
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            return intent.getClientSecret();
        } catch (StripeException e) {
            throw new PaymentFailedException("Stripe payment failed", e);
        }
    }
}
