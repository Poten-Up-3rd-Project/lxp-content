package com.lxp.content.course.application.port.provider.usecase.query;

import com.lxp.common.application.port.in.QueryUseCase;
import com.lxp.content.course.application.port.provider.query.CheckCourseExistsQuery;

public interface CheckCourseExistsUseCase
        extends QueryUseCase<CheckCourseExistsQuery, Boolean> {
}
