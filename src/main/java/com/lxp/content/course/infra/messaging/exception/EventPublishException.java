package com.lxp.content.course.infra.messaging.exception;

import com.lxp.content.common.exception.ExternalServiceException;

public class EventPublishException extends ExternalServiceException {
    public EventPublishException(String message) {
        super("MessageBroker", message);
    }

    public EventPublishException(String message, Throwable cause) {
        super("MessageBroker", message, cause);
    }
}