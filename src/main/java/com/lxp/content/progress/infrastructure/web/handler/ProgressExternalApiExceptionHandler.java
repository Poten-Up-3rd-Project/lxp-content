package com.lxp.content.progress.infrastructure.web.handler;

import com.lxp.common.infrastructure.exception.ApiResponse;
import com.lxp.common.infrastructure.exception.ErrorResponse;
import com.lxp.content.progress.exception.ProgressDomainException;
import com.lxp.content.progress.exception.ProgressErrorCode;
import com.lxp.content.progress.infrastructure.web.external.ProgressExternalController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 외부 Progress API 예외 핸들러
 */
@RestControllerAdvice(assignableTypes = ProgressExternalController.class)
public class ProgressExternalApiExceptionHandler {

    @ExceptionHandler(ProgressDomainException.class)
    public ResponseEntity<ApiResponse<Void>> handleProgressDomainException(ProgressDomainException e) {
        ProgressErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(
                new ErrorResponse(
                    errorCode.getCode(),
                    errorCode.getMessage(),
                    errorCode.getGroup()
                )
            ));

    }

}
