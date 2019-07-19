package com.revolut.exception;

public class InsufficientBalanceException extends RevolutException {
    public InsufficientBalanceException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
