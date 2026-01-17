package com.lxp.content.progress.application.port.out.dto;

import java.util.List;

public record CourseLectureInfo (
        String courseId,
        List<LectureInfo> lectures
) {
    public record LectureInfo (
            String lectureId,
            Integer durationInMinutes
    ) {}
}
