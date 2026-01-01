package com.lxp.content.course.domain.model.vo.duration;

public record SectionDuration(long seconds) {
    public SectionDuration {
        if (seconds <= 0) {
            throw new IllegalArgumentException("section duration must be positive");
        }
    }

    public long toMinutes() {
        return seconds / 60;
    }
}