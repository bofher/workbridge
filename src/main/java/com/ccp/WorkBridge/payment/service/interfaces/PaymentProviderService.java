package com.ccp.WorkBridge.payment.service.interfaces;

import com.ccp.WorkBridge.shared.exceptions.PaymentFailedException;
import com.ccp.WorkBridge.payment.Payment;

public interface PaymentProviderService {
    String createPaymentIntent(Payment payment) throws PaymentFailedException;
}
