package com.ccp.WorkBridge.services.payment;

import com.ccp.WorkBridge.enums.PaymentProviderType;
import com.ccp.WorkBridge.enums.PaymentStatus;
import com.ccp.WorkBridge.exceptions.DataNotFoundException;
import com.ccp.WorkBridge.exceptions.PaymentFailedException;
import com.ccp.WorkBridge.models.Order;
import com.ccp.WorkBridge.models.Payment;
import com.ccp.WorkBridge.models.Subscription;
import com.ccp.WorkBridge.models.User;
import com.ccp.WorkBridge.repos.PaymentRepository;
import com.ccp.WorkBridge.services.interfaces.PaymentProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

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

    public void markAsPaid(String externalPaymentId) {
        Payment payment = paymentRepository.findByExternalPaymentId(externalPaymentId)
                .orElseThrow(()-> new DataNotFoundException("Payment not found"));

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(Instant.now());

        paymentRepository.save(payment);
    }

    public void markAsFailed(String externalPaymentId, String reason) {
        Payment payment = paymentRepository.findByExternalPaymentId(externalPaymentId)
                .orElseThrow(() -> new DataNotFoundException("Payment not found"));

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(reason);
        paymentRepository.save(payment);
    }
}
