package com.lxp.content.progress.domain.model.vo;

/**
 * 강의 ID(임시로 여기 넣어둠)
 * @param value 강의 ID 값
 */
public record LectureId (String value) {
    public LectureId {
        if (value != null && value.isBlank()) {
            throw new IllegalArgumentException("LectureId cannot be null and blank");
        }
    }

    public static LectureId from(String value) {
        return new LectureId(value);
    }
}
