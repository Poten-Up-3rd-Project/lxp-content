package com.lxp.content.course.application.service.query.external;

import com.lxp.content.course.application.mapper.CourseViewMapper;
import com.lxp.content.course.application.port.provider.query.CourseDetailQuery;
import com.lxp.content.course.application.port.provider.usecase.query.CourseDetailUseCase;
import com.lxp.content.course.application.port.provider.view.CourseDetailView;
import com.lxp.content.course.application.port.required.TagQueryPort;
import com.lxp.content.course.application.port.required.UserQueryPort;
import com.lxp.content.course.domain.model.id.TagId;
import com.lxp.content.course.domain.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseDetailService implements CourseDetailUseCase {

    private final CourseRepository courseRepository;
    private final UserQueryPort userQueryPort;
    private final TagQueryPort tagQueryPort;
    private final CourseViewMapper courseViewMapper;

    @Override
    public CourseDetailView execute(CourseDetailQuery query) {
        return courseRepository.findByUUID(query.courseId())
                .map(course -> courseViewMapper.toCourseDetailView(
                        course,
                        tagQueryPort.findTagByIds(course.tags().values().stream().map(TagId::value).toList()),
                        userQueryPort.getInstructorInfo(course.instructorUUID().value())
                ))
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + query.courseId()));
    }
}
