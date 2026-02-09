package com.lxp.content.resource.domain.exception;

import com.lxp.common.domain.exception.DomainException;

public class ResourceException extends DomainException {

    public ResourceException(ResourceErrorCode errorCode) {
        super(errorCode);
    }

    public ResourceException(ResourceErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public ResourceException(ResourceErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
