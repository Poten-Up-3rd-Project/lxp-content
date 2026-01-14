package com.lxp.content.course.application.service.query.external;

import com.lxp.common.domain.pagination.Page;
import com.lxp.common.domain.pagination.PageRequest;
import com.lxp.content.course.application.mapper.CourseViewMapper;
import com.lxp.content.course.application.port.provider.query.CourseSearchQuery;
import com.lxp.content.course.application.port.provider.usecase.query.CourseSearchUseCase;
import com.lxp.content.course.application.port.provider.view.CourseView;
import com.lxp.content.course.application.projection.CourseReadModel;
import com.lxp.content.course.application.projection.repository.CourseReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseSearchService implements CourseSearchUseCase {
    private final CourseReadRepository courseReadRepository;
    private final CourseViewMapper courseViewMapper;


    @Override
    public Page<CourseView> execute(CourseSearchQuery query) {
        if(query.keyword() != null && !query.keyword().isEmpty()) {
            return searchCourses(query.keyword(), query.pageRequest());
        }

        Page<CourseReadModel> course = courseReadRepository.findAll(query.pageRequest());
        return courseViewMapper.toPageView(course);
    }

    private Page<CourseView> searchCourses(String keyword, PageRequest pageRequest) {
        Page<CourseReadModel> course = courseReadRepository.search(keyword, pageRequest);
        return courseViewMapper.toPageView(course);
    }
}
