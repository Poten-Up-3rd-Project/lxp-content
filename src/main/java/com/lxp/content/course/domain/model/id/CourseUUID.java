package com.lxp.content.course.domain.model.id;

public record CourseUUID(String value) {
    public CourseUUID {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CourseId must be not null");
        }
    }
}