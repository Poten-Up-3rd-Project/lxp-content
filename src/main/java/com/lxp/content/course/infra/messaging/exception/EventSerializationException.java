package com.lxp.content.course.infra.messaging.exception;

public class EventSerializationException extends RuntimeException {
    public EventSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
