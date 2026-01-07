package com.lxp.content.course.application.port.provider.result;


import java.util.List;

public record CourseResult(
        String courseUUID,
        Long courseId,
        String instructorUUID,
        String title,
        String thumbnailUrl,
        String description,
        String difficulty, // JUNIOR, MIDDLE, SENIOR, EXPERT
        List<Long> tags
) {
}
