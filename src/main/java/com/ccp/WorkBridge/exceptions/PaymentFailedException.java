package com.ccp.WorkBridge.exceptions;

public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(String message, Throwable cause) { super(message, cause); }
}
