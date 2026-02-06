package com.lxp.content.resource.domain.exception;

import com.lxp.common.domain.exception.ErrorCode;

public enum ResourceErrorCode implements ErrorCode {

    FILE_SIZE_EXCEEDED("BAD_REQUEST", "RESOURCE_001", "파일 크기가 허용된 최대 용량을 초과했습니다."),
    MISSING_REQUIRED_FIELD("BAD_REQUEST", "RESOURCE_002", "필수 입력 항목이 누락되었습니다."),
    INVALID_STATUS_TRANSITION("BAD_REQUEST", "RESOURCE_003", "리소스 상태 전이가 허용되지 않습니다."),
    RESOURCE_NOT_FOUND("NOT_FOUND", "RESOURCE_004", "파일 정보를 찾을 수 없습니다."),
    UNSUPPORTED_CONTENT_TYPE("BAD_REQUEST", "RESOURCE_005", "지원하지 않는 콘텐츠 타입입니다."),
    ;

    private final String group;
    private final String code;
    private final String message;

    ResourceErrorCode(String group, String code, String message) {
        this.group = group;
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getGroup() {
        return this.group;
    }
}
