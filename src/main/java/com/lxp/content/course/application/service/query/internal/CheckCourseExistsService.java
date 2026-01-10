package com.lxp.content.course.application.service.query.internal;

import com.lxp.content.course.application.port.provider.query.interanl.CheckCourseExistsQuery;
import com.lxp.content.course.application.port.provider.usecase.query.internal.CheckCourseExistsUseCase;
import com.lxp.content.course.application.projection.repository.CourseReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckCourseExistsService implements CheckCourseExistsUseCase {

    private final CourseReadRepository courseReadRepository;

    @Override
    public Boolean execute(CheckCourseExistsQuery query) {
        return courseReadRepository.existsById(query.courseId());
    }
}
