package com.lxp.content.common.exception;

import com.lxp.common.infrastructure.exception.ErrorResponse;
import com.lxp.common.infrastructure.exception.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ContentExceptionHandler extends GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining(", "));

        log.warn("Validation failed: {}", message);

        return ResponseEntity.badRequest().body(
                new ErrorResponse("VALIDATION_ERROR", message, "INVALID")
        );
    }


    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponse> handleExternalApi(ExternalApiException e) {
        log.error("External API error [{}]: status={}, message={}",
                e.getServiceName(), e.getStatusCode(), e.getMessage());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
                new ErrorResponse(
                        "EXTERNAL_API_ERROR",
                        e.getServiceName() + " 서비스 호출 실패",
                        "INTERNAL_SERVER_ERROR"
                )
        );
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalService(ExternalServiceException e) {
        log.error("External service error [{}]: {}", e.getServiceName(), e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
                new ErrorResponse(
                        "SERVICE_UNAVAILABLE",
                        e.getServiceName() + " 서비스에 연결할 수 없습니다",
                        "INTERNAL_SERVER_ERROR"
                )
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.warn("Method not supported: {}", e.getMethod());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
                new ErrorResponse("METHOD_NOT_ALLOWED", "지원하지 않는 HTTP 메서드입니다", "BAD_REQUEST")
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("Illegal argument: {}", e.getMessage());

        return ResponseEntity.badRequest().body(
                new ErrorResponse("INVALID_ARGUMENT", e.getMessage(), "BAD_REQUEST")
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unexpected error", e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다", "INTERNAL_SERVER_ERROR")
        );
    }


    private String formatFieldError(FieldError error) {
        return String.format("%s: %s", error.getField(), error.getDefaultMessage());
    }
}
