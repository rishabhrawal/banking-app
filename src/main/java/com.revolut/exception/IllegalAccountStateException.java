package com.revolut.exception;

public class IllegalAccountStateException extends RevolutException {
    public IllegalAccountStateException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
