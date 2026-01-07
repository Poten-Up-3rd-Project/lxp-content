package com.lxp.content.progress.domain.model.enums;

/**
 * 강좌 진행 상태 정의 Enum
 */
public enum CourseProgressStatus {
    IDLE("강좌 수강 전"),
    IN_PROGRESS("강좌 수강 중"),
    COMPLETED("강좌 수강 완료");

    private final String description;

    CourseProgressStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
