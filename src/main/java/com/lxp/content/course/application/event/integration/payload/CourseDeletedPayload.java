package com.lxp.content.course.application.event.integration.payload;

import java.time.LocalDateTime;

public record CourseDeletedPayload(
        String courseUuid,
        LocalDateTime deletedAt
) {
}
