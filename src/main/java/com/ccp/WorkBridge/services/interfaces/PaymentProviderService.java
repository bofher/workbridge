package com.ccp.WorkBridge.services.interfaces;

import com.ccp.WorkBridge.models.Payment;

public interface PaymentProviderService {
    String createPaymentIntent(Payment payment) throws Exception;
}
