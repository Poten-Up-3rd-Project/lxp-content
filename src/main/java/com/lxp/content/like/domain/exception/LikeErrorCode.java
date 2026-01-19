package com.lxp.content.like.domain.exception;

import com.lxp.common.domain.exception.ErrorCode;

public enum LikeErrorCode implements ErrorCode {

    USER_ID_IS_NULL("LIKE_001", "must be not null: userId", "CONFLICT"),
    COURSE_ID_IS_NULL("LIKE_002", "must be not null: courseId", "CONFLICT"),
    COURSE_NOT_FOUND("LIKE_003", "강좌를 찾을 수 없습니다.", "NOT_FOUND");

    private final String code;
    private final String message;
    private final String group;

    LikeErrorCode(String code, String message, String group) {
        this.code = code;
        this.message = message;
        this.group = group;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getGroup() {
        return group;
    }
}
