package com.lxp.content.course.infra.web.external.dto.response;

import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
public class CourseResponse {
    String id;
    InstructorResponse instructor;
    String title;
    String description;
    String thumbnailUrl;
    EnumResponse level;
    List<TagResponse> tags;
    Instant createdAt;
    Instant updatedAt;

    public CourseResponse(
            String id,
            InstructorResponse instructor,
            String title,
            String description,
            String thumbnailUrl,
            EnumResponse level,
            List<TagResponse> tags,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.instructor = instructor;
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.level = level;
        this.tags = tags;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
