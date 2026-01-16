package com.lxp.content.course.application.port.provider.query;

import com.lxp.common.application.cqrs.Query;
import com.lxp.common.domain.pagination.Page;
import com.lxp.common.domain.pagination.PageRequest;
import com.lxp.content.course.application.port.provider.view.CourseView;

public record CourseSearchQuery(
        String keyword,
        PageRequest pageRequest
) implements Query<Page<CourseView>> {
}