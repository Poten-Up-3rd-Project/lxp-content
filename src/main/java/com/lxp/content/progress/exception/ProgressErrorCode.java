package com.lxp.content.progress.exception;

import org.springframework.http.HttpStatus;

/**
 * Progress 관련 Exception 코드
 */
public enum ProgressErrorCode {

    COURSE_PROGRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "PROGRESS_001", "PROGRESS", "정보에 해당하는 강좌 진행률이 존재하지 않습니다"),
    LECTURE_PROGRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "PROGRESS_002", "PROGRESS", "정보에 해당하는 강의 진행률이 존재하지 않습니다"),
    INVALID_LAST_PLAYED_TIME_VALUE(HttpStatus.BAD_REQUEST, "PROGRESS_003", "PROGRESS", "마지막 재생 시간은 0이상 전체 강의 시간 이하여야 합니다"),
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "PROGRESS_004", "PROGRESS", "정보에 해당하는 강좌가 존재하지 않습니다");

    private final HttpStatus status;
    private final String code;
    private final String group;
    private final String message;

    ProgressErrorCode(HttpStatus status, String code, String group, String message) {
        this.status = status;
        this.code = code;
        this.group = group;
        this.message = message;
    }

    public HttpStatus getStatus() { return status; }
    public String getCode() { return code; }
    public String getGroup() { return group; }
    public String getMessage() { return message; }

}
