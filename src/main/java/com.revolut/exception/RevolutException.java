package com.revolut.exception;

public class RevolutException extends Exception {
    private int code;

    public int getCode() {
        return code;
    }

    public RevolutException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
