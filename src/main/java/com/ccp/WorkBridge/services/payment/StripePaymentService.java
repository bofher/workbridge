package com.ccp.WorkBridge.services.payment;

import com.ccp.WorkBridge.enums.PaymentStatus;
import com.ccp.WorkBridge.models.Payment;

import com.ccp.WorkBridge.repos.PaymentRepository;
import com.ccp.WorkBridge.services.interfaces.PaymentProviderService;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class StripePaymentService implements PaymentProviderService {
    private final PaymentRepository paymentRepository;

    @Override
    public String createPaymentIntent(Payment payment) throws Exception {
        if (payment.getExternalPaymentId() != null) {
            throw new IllegalStateException("Payment already has external id");
        }
        long amountInCents = payment.getAmount()
                .multiply(BigDecimal.valueOf(100))
                .longValue();
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(payment.getCurrency().toLowerCase())
                .putMetadata("paymentId", payment.getId().toString())
                .build();
        PaymentIntent intent = PaymentIntent.create(params);

        payment.setExternalPaymentId(intent.getId());
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);

        return intent.getClientSecret();
    }
}
