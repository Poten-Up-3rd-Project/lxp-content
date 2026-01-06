package com.lxp.content.course.infra.event.integration.payload;

import java.util.List;

public record CourseCreatedPayload (
        String courseUuid,
        String instructorUuid,
        String title,
        String description,
        String thumbnailUrl,
        String difficulty,
        List<Long> tagIds
){}