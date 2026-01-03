package com.lxp.content.course.domain.model.id;

public record InstructorUUID(String value) {
    public InstructorUUID {
        if (value != null && value.isBlank()) {
            throw new IllegalArgumentException("UserId must be positive.");
        }
    }
}
