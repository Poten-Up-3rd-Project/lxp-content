package com.lxp.content.course.application.port.provider.query;

import java.util.List;

public record CourseFilterQuery(
        List<String> courseIds,
        List<String> difficulties,
        int count
) {
    public CourseFilterQuery {
        count = Math.max(1, Math.min(count, 100));
    }
}
