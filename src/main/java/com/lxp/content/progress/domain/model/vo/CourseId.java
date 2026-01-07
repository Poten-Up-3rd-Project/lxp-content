package com.lxp.content.progress.domain.model.vo;

/**
 * 강의 ID(임시로 여기 넣어둠)
 * @param value 강의 ID 값
 */
public record CourseId(String value) {
    public CourseId {
        if (value != null && value.isEmpty()) {
            throw new IllegalArgumentException("UserId must be not empty");
        }
    }
}
