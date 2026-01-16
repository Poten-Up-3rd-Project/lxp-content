package com.lxp.content.common.passport;


public class InvalidPassportException extends RuntimeException {

    public InvalidPassportException(String message) {
        super(message);
    }

    public InvalidPassportException(String message, Throwable cause) {
        super(message, cause);
    }
}