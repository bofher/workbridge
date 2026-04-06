package com.ccp.WorkBridge.services.payment;

import com.ccp.WorkBridge.enums.PaymentProviderType;
import com.ccp.WorkBridge.enums.PaymentStatus;
import com.ccp.WorkBridge.exceptions.PaymentFailedException;
import com.ccp.WorkBridge.models.Order;
import com.ccp.WorkBridge.models.Payment;
import com.ccp.WorkBridge.models.Subscription;
import com.ccp.WorkBridge.models.User;
import com.ccp.WorkBridge.repos.PaymentRepository;
import com.ccp.WorkBridge.services.interfaces.PaymentProviderService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentProviderService paymentProviderService;
    private final PaymentRepository paymentRepository;

    public Payment createPayment(User user, BigDecimal amount, String currency,
                                 Order order, Subscription subscription) {

        Payment payment = Payment.builder()
                .user(user)
                .amount(amount)
                .currency(currency)
                .status(PaymentStatus.PENDING)
                .provider(PaymentProviderType.STRIPE)
                .order(order)
                .subscription(subscription)
                .build();

        return paymentRepository.save(payment);
    }

    public String processPayment(Payment payment) {
        try {
            String clientSecret = paymentProviderService.createPaymentIntent(payment);
            payment.setExternalPaymentId(clientSecret);
            payment.setStatus(PaymentStatus.PENDING);
            paymentRepository.save(payment);

            return clientSecret;
        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(e.getMessage());
            paymentRepository.save(payment);
            throw new PaymentFailedException("Payment failed:",e);
        }

    }
}
