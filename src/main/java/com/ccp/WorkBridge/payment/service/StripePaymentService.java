package com.ccp.WorkBridge.payment.service;

import com.ccp.WorkBridge.payment.dto.PaymentIntentResult;
import com.ccp.WorkBridge.payment.model.OrderPayment;
import com.ccp.WorkBridge.shared.exceptions.PaymentFailedException;
import com.ccp.WorkBridge.payment.service.interfaces.PaymentProviderService;
import com.stripe.exception.StripeException;
import com.stripe.model.BalanceTransaction;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Transfer;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.TransferCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripePaymentService implements PaymentProviderService {

    private static final BigDecimal PLATFORM_FEE_PERCENTAGE = new BigDecimal("0.10");

    @Override
    public PaymentIntentResult createOrderPaymentIntent(OrderPayment payment) {
        return createIntent(payment.getAmount(), payment.getCurrency(), payment.getId());
    }

    @Override
    public boolean capturePayment(String externalPaymentId) {
        try {
            PaymentIntent intent = PaymentIntent.retrieve(externalPaymentId);
            intent.capture();
            log.info("Payment {} captured. Funds in platform account", externalPaymentId);
            return true;
        } catch (StripeException e) {
            log.error("Capture failed: {}", e.getMessage(), e);
            throw new PaymentFailedException("Capture failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean cancelPayment(String externalPaymentId) {
        try {
            PaymentIntent intent = PaymentIntent.retrieve(externalPaymentId);
            intent.cancel();
            log.info("Payment {} canceled", externalPaymentId);
            return true;
        } catch (StripeException e) {
            log.error("Cancel failed: {}", e.getMessage(), e);
            throw new PaymentFailedException("Cancel failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String transferToFreelancer(OrderPayment payment, String freelancerStripeAccountId) {
        try {
            PaymentIntent intent = PaymentIntent.retrieve(payment.getExternalPaymentId());
            Charge charge = Charge.retrieve(intent.getLatestCharge());

            if (!Boolean.TRUE.equals(charge.getPaid())) {
                throw new PaymentFailedException("Charge is not paid");
            }
            BalanceTransaction balanceTx = BalanceTransaction.retrieve(charge.getBalanceTransaction());
            String currency = balanceTx.getCurrency().toLowerCase();

            long totalAmount = Math.abs(balanceTx.getAmount());
            long platformFee = BigDecimal.valueOf(totalAmount)
                    .multiply(PLATFORM_FEE_PERCENTAGE).setScale(0, RoundingMode.HALF_UP).longValue();
            long freelancerAmount = totalAmount - platformFee;

            TransferCreateParams params = TransferCreateParams.builder()
                    .setAmount(freelancerAmount)
                    .setCurrency(currency)
                    .setDestination(freelancerStripeAccountId)
                    .setSourceTransaction(charge.getId()).build();
            Transfer transfer = Transfer.create(params);
            log.info("Transfer created. TransferId={}, amount={}, currency={}",
                    transfer.getId(), freelancerAmount, currency);
            return transfer.getId();

        } catch (StripeException e) {
            throw new PaymentFailedException("Transfer failed: " + e.getMessage(), e);
        }
    }

    private PaymentIntentResult createIntent(BigDecimal amount, String currency, Long paymentId) {
        try {
            long amountInCents = convertToStripeAmount(amount);

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder().setAmount(amountInCents).setCurrency(currency.toLowerCase()).setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL).putMetadata("paymentId", String.valueOf(paymentId)).build();

            PaymentIntent intent = PaymentIntent.create(params);

            log.info("PaymentIntent created (MANUAL capture): {}. " + "Amount: {} {}", intent.getId(), amount, currency);

            return new PaymentIntentResult(intent.getId(), intent.getClientSecret());
        } catch (StripeException e) {
            log.error("PaymentIntent creation failed: {}", e.getMessage(), e);
            throw new PaymentFailedException("PaymentIntent creation failed: " + e.getMessage(), e);
        }
    }

    private long convertToStripeAmount(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }
}