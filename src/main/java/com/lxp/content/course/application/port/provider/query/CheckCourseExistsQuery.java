package com.lxp.content.course.application.port.provider.query;

import com.lxp.common.application.cqrs.Query;

public record CheckCourseExistsQuery(String courseId) implements Query<Boolean> {
}
