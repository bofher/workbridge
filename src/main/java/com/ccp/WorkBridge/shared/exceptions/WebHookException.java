package com.ccp.WorkBridge.shared.exceptions;

public class WebHookException extends RuntimeException {
    public WebHookException(String message) {
        super(message);
    }
}
