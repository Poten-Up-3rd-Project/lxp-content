package com.lxp.content.course.application.service.query.internal;

import com.lxp.content.course.application.mapper.CourseViewMapper;
import com.lxp.content.course.application.port.provider.query.CourseFilterQuery;
import com.lxp.content.course.application.port.provider.usecase.query.CourseFilterUseCase;
import com.lxp.content.course.application.port.provider.view.CourseView;
import com.lxp.content.course.application.projection.repository.CourseReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseFilterService implements CourseFilterUseCase {
    private final CourseReadRepository courseReadRepository;
    private final CourseViewMapper mapper;

    @Override
    public List<CourseView> execute(CourseFilterQuery query) {
        return courseReadRepository.filter(
                query.courseIds(),
                query.difficulties(),
                query.count()
        ).stream().map(mapper::toListView).toList();
    }

}
