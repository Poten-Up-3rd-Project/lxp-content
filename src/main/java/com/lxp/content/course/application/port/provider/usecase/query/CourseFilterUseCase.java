package com.lxp.content.course.application.port.provider.usecase.query;

import com.lxp.common.application.port.in.QueryUseCase;
import com.lxp.content.course.application.port.provider.query.CourseFilterQuery;
import com.lxp.content.course.application.port.provider.view.CourseView;

import java.util.List;

public interface CourseFilterUseCase extends QueryUseCase<CourseFilterQuery, List<CourseView>> {

}
