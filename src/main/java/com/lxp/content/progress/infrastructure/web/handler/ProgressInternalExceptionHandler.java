package com.lxp.content.progress.infrastructure.web.handler;

import com.lxp.content.progress.exception.ProgressDomainException;
import com.lxp.content.progress.infrastructure.web.internal.ProgressInternalController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 내부 Progress API 예외 핸들러
 */
@RestControllerAdvice(assignableTypes = ProgressInternalController.class)
public class ProgressInternalExceptionHandler {



    @ExceptionHandler(ProgressDomainException.class)
    public ResponseEntity<String> handleProgressDomainException(ProgressDomainException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

}
