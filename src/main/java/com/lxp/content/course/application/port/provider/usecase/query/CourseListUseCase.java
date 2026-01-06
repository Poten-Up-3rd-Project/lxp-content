package com.lxp.content.course.application.port.provider.usecase.query;

import com.lxp.common.application.port.in.QueryUseCase;
import com.lxp.common.domain.pagination.Page;
import com.lxp.content.course.application.port.provider.query.CourseListQuery;
import com.lxp.content.course.application.port.provider.view.CourseView;

public interface CourseListUseCase extends QueryUseCase<CourseListQuery, Page<CourseView>> {
}
