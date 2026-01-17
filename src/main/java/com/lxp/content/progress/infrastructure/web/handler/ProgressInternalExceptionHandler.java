package com.lxp.content.progress.infrastructure.web.handler;

import com.lxp.content.progress.exception.ProgressDomainException;
import com.lxp.content.progress.exception.ProgressErrorCode;
import com.lxp.content.progress.infrastructure.web.internal.ProgressInternalController;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * 내부 Progress API 예외 핸들러
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = ProgressInternalController.class)
public class ProgressInternalExceptionHandler {



    @ExceptionHandler(ProgressDomainException.class)
    public ResponseEntity<Map<String, Object>> handleProgressDomainException(ProgressDomainException e) {
        ProgressErrorCode errorCode = e.getErrorCode();

        // 텍스트가 아닌 JSON 객체 구조를 생성합니다.
        Map<String, Object> body = Map.of(
                "success", false,
                "error", Map.of(
                        "code", errorCode.getCode(),
                        "group", errorCode.getGroup(),
                        "message", errorCode.getMessage()
                )
        );

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(body); // 이제 JSON으로 변환되어 나갑니다.
    }

}
