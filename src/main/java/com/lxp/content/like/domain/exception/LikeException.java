package com.lxp.content.like.domain.exception;

import com.lxp.common.domain.exception.DomainException;
import com.lxp.common.domain.exception.ErrorCode;

public class LikeException extends DomainException {
    public LikeException(ErrorCode errorCode) {
        super(errorCode);
    }

    public LikeException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public LikeException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
