package com.lxp.content.course.domain.model.id;

public record LectureUUID(String value) {
    public LectureUUID {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("LectureId must be positive.");
        }
    }
}
