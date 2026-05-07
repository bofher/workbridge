package com.ccp.WorkBridge.payment.service.interfaces;

import com.ccp.WorkBridge.payment.dto.PaymentIntentResult;
import com.ccp.WorkBridge.payment.model.OrderPayment;
import com.ccp.WorkBridge.payment.model.SubscriptionPayment;

import java.math.BigDecimal;

public interface PaymentProviderService {
    PaymentIntentResult createOrderPaymentIntent(OrderPayment payment);
    boolean capturePayment(String externalPaymentId);
    boolean cancelPayment(String externalPaymentId);

    String transferToFreelancer(OrderPayment payment, String stripeAccountId);
}