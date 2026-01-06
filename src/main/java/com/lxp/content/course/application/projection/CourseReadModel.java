package com.lxp.content.course.application.projection;

import com.lxp.content.course.domain.model.enums.Level;

import java.time.LocalDateTime;
import java.util.List;

public record CourseReadModel(
        String uuid,
        String instructorId,
        String instructorName,
        String thumbnailUrl,
        String title,
        String description,
        Level difficulty,
        List<TagReadModel> tags,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public record TagReadModel(
            Long id,
            String content,
            String color,
            String variant
    ){}
}
