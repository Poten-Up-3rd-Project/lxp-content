package com.lxp.content.progress.domain.model.vo;

import com.lxp.common.domain.model.ValueObject;

/**
 * 강의 진행률 ID
 */
public class CourseProgressId extends ValueObject {

    private final String value;

    private CourseProgressId(String value) {
        this.value = value;
    }

    /**
     * 강의 진행률 ID 생성
     * @return CourseProgressId 강좌 진행률 ID 객체
     */
    public static CourseProgressId create() {
        return new CourseProgressId(java.util.UUID.randomUUID().toString());
    }
    public static CourseProgressId create(String value) {
        return new CourseProgressId(value);
    }

    public String value() {
        return value;
    }

    @Override
    protected Object[] getEqualityComponents() {
        return new Object[]{value};
    }
}
