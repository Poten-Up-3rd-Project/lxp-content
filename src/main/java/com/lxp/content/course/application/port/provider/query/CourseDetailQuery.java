package com.lxp.content.course.application.port.provider.query;

import com.lxp.common.application.cqrs.Query;
import com.lxp.content.course.application.port.provider.view.CourseDetailView;

public record CourseDetailQuery(String courseId) implements Query<CourseDetailView> {
}
