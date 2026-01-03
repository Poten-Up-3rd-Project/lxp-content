package com.lxp.content.course.domain.model.vo.duration;

public record CourseDuration(long seconds) {

    public CourseDuration {
        if (seconds <= 0) {
            throw new IllegalArgumentException("course duration must be positive");
        }
    }

    public long toMinutes() {
        return seconds / 60;
    }
}
