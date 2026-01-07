package com.lxp.content.progress.domain.model.enums;

/**
 * 강의 진행 상태 정의 enum
 */
public enum LectureProgressStatus {
    NOT_STARTED("강의 수강 전"),
    IN_PROGRESS("강의 수강 중"),
    COMPLETED("강의 수강 완료");

    private final String description;

    LectureProgressStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
