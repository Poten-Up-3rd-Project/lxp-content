package com.lxp.content.course.application.port.provider.usecase.query.internal;

import com.lxp.common.application.port.in.QueryUseCase;
import com.lxp.content.course.application.port.provider.query.interanl.CheckCourseExistsQuery;

public interface CheckCourseExistsUseCase
        extends QueryUseCase<CheckCourseExistsQuery, Boolean> {
}
