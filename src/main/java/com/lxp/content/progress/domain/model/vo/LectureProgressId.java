package com.lxp.content.progress.domain.model.vo;

import com.lxp.common.domain.model.ValueObject;

import java.util.UUID;

/**
 * 강좌 진행률 ID
 */
public class LectureProgressId extends ValueObject {

    private final String value;

    private LectureProgressId(String value) {
        this.value = value;
    }

    /**
     * 강좌 진행률 ID 생성
     * @return LectureProgressId 강의 진행률 ID 객체
     */
    public static LectureProgressId create() {
        return new LectureProgressId(UUID.randomUUID().toString());
    }
    public static LectureProgressId create(String value) {
        return new LectureProgressId(value);
    }

    public String value() {
        return value;
    }

    @Override
    protected Object[] getEqualityComponents() {
        return new Object[]{value};
    }

}
