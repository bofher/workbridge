package com.ccp.WorkBridge.shared.exceptions;

public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(String message, Throwable cause) { super(message, cause); }

    public PaymentFailedException(String message) { super(message); }
}
