package com.lxp.content.common.exception;

public class ExternalApiException extends ExternalServiceException {
    private final int statusCode;

    public ExternalApiException(String serviceName, int statusCode, String message) {
        super(serviceName, message);
        this.statusCode = statusCode;
    }

    public ExternalApiException(String serviceName, String message, Throwable cause) {
        super(serviceName, message, cause);
        this.statusCode = 0;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
