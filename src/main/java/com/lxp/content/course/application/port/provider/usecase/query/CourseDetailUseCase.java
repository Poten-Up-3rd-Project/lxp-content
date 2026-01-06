package com.lxp.content.course.application.port.provider.usecase.query;

import com.lxp.common.application.port.in.QueryUseCase;
import com.lxp.content.course.application.port.provider.query.CourseDetailQuery;
import com.lxp.content.course.application.port.provider.view.CourseDetailView;

public interface CourseDetailUseCase extends QueryUseCase<CourseDetailQuery, CourseDetailView> {
}

