package com.lxp.content.progress.exception;

import com.lxp.content.common.exception.ExternalServiceException;
import org.springframework.http.HttpStatus;

/**
 * Progress 도메인 예외 클래스
 */
public class ProgressDomainException extends ExternalServiceException {

    private final ProgressErrorCode errorCode;

    public ProgressDomainException(ProgressErrorCode errorCode) {
        super("PROGRESS-DOMAIN", errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ProgressErrorCode getErrorCode() {
        return errorCode;
    }

}
