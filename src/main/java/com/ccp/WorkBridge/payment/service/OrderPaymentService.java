package com.ccp.WorkBridge.payment.service;

import com.ccp.WorkBridge.order.Order;
import com.ccp.WorkBridge.payment.dto.PaymentIntentResult;
import com.ccp.WorkBridge.payment.model.OrderPayment;
import com.ccp.WorkBridge.payment.model.enums.PaymentProviderType;
import com.ccp.WorkBridge.payment.model.enums.PaymentStatus;
import com.ccp.WorkBridge.payment.repo.OrderPaymentRepository;
import com.ccp.WorkBridge.payment.service.interfaces.PaymentProviderService;
import com.ccp.WorkBridge.shared.exceptions.DataNotFoundException;
import com.ccp.WorkBridge.shared.exceptions.PaymentFailedException;
import com.ccp.WorkBridge.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderPaymentService {

    private final PaymentProviderService paymentProviderService;
    private final OrderPaymentRepository paymentRepository;

    public OrderPayment createPayment(User user, BigDecimal amount, String currency, Order order) {
        OrderPayment payment = new OrderPayment();
        payment.setUser(user);
        payment.setAmount(amount);
        payment.setCurrency(currency);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setProvider(PaymentProviderType.STRIPE);
        payment.setOrder(order);
        payment.setFreelancer(order.getFreelancer());

        log.info("Creating payment: {} {}, Order: {}", amount, currency, order.getId());
        return paymentRepository.save(payment);
    }

    @Transactional
    public PaymentIntentResult processPayment(Long paymentId) {
        OrderPayment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new DataNotFoundException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment already processed");
        }
        log.info("Processing payment {} (MANUAL capture)", paymentId);
        PaymentIntentResult result = paymentProviderService.createOrderPaymentIntent(payment);

        payment.setExternalPaymentId(result.paymentIntentId());
        payment.setStatus(PaymentStatus.INITIATED);
        paymentRepository.save(payment);

        log.info("Payment {} initiated", paymentId);
        return result;
    }

    @Transactional
    public void capturePayment(String externalPaymentId) {
        OrderPayment payment = paymentRepository.findByExternalPaymentId(externalPaymentId)
                .orElseThrow(() -> new DataNotFoundException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.INITIATED) {
            throw new IllegalStateException(
                    "Payment must be INITIATED to capture. Current: " + payment.getStatus()
            );
        }
        log.info("Capturing payment {}", externalPaymentId);
        boolean captured = paymentProviderService.capturePayment(externalPaymentId);

        if (captured) {
            payment.setStatus(PaymentStatus.PAID);
            payment.setPaidAt(Instant.now());
            paymentRepository.save(payment);
            log.info("Payment {} captured. Funds in platform account", externalPaymentId);
        } else {
            throw new PaymentFailedException("Failed to capture payment");
        }
    }

    @Transactional
    public void markAsPaid(String externalPaymentId) {
        OrderPayment payment = paymentRepository.findByExternalPaymentId(externalPaymentId)
                .orElseThrow(() -> new DataNotFoundException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.PAID) {
            return;
        }
        if (payment.getStatus() == PaymentStatus.INITIATED) {
            capturePayment(externalPaymentId);
        } else {
            throw new IllegalStateException(
                    "Payment must be INITIATED or PAID. Current: " + payment.getStatus()
            );
        }

        log.info("Payment {} confirmed as paid", externalPaymentId);
    }

    @Transactional
    public void markAsFailed(String externalPaymentId, String reason) {
        OrderPayment payment = paymentRepository.findByExternalPaymentId(externalPaymentId)
                .orElseThrow(() -> new DataNotFoundException("Payment not found"));

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(reason);
        paymentRepository.save(payment);
        log.warn("Payment {} failed: {}", externalPaymentId, reason);
    }

    @Transactional
    public void completePayment(Long paymentId) {
        OrderPayment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new DataNotFoundException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new PaymentFailedException(
                    "Payment must be PAID. Current: " + payment.getStatus()
            );
        }
        User freelancer = payment.getFreelancer();

        if (freelancer == null || freelancer.getStripeConnectAccount() == null) {
            throw new PaymentFailedException("Freelancer has no Stripe Connect account");
        }
        String stripeAccountId = freelancer.getStripeConnectAccount().getStripeAccountId();
        String transferId = paymentProviderService.transferToFreelancer(payment, stripeAccountId);

        payment.setStripeTransferId(transferId);
        payment.setStatus(PaymentStatus.TRANSFERRED);
        paymentRepository.save(payment);

        log.info("Payment {} transferred to freelancer. Transfer ID: {}", paymentId, transferId);
    }

    @Transactional
    public void cancelPayment(String externalPaymentId) {
        OrderPayment payment = paymentRepository.findByExternalPaymentId(externalPaymentId)
                .orElseThrow(() -> new DataNotFoundException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.TRANSFERRED) {
            throw new PaymentFailedException("Cannot cancel transferred payment");
        }

        log.info("Canceling payment {}", externalPaymentId);
        paymentProviderService.cancelPayment(externalPaymentId);

        payment.setStatus(PaymentStatus.CANCELED);
        paymentRepository.save(payment);
    }

    @Transactional
    public void completeOrderAndTransfer(Long orderId) {
        OrderPayment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new DataNotFoundException("Payment not found for order"));

        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new PaymentFailedException(
                    "Payment must be PAID before transfer. Current: " + payment.getStatus()
            );
        }

        completePayment(payment.getId());
        log.info("Order {} completed and payment transferred to freelancer", orderId);
    }

    @Transactional
    public void confirmOrderReady(Long orderId) {
        OrderPayment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new DataNotFoundException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new PaymentFailedException(
                    "Order cannot be marked ready without payment"
            );
        }
        log.info("Order {} confirmed as ready (payment is secure in platform account)", orderId);
    }
}