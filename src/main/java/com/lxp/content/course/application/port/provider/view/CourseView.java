package com.lxp.content.course.application.port.provider.view;

import com.lxp.content.course.domain.model.enums.Level;

import java.time.Instant;
import java.util.List;

public record CourseView(
        String courseId,
        String title,
        String description,
        InstructorView Instructor,
        String thumbnailUrl,
        Level level,
        Instant createdAt,
        Instant updatedAt,
        List<TagInfoView> tags
) {
}
