package com.lxp.content.course.infra.persistence.mysql.read.adapter;

import com.lxp.common.domain.pagination.Page;
import com.lxp.common.domain.pagination.PageRequest;
import com.lxp.common.infrastructure.persistence.PageConverter;
import com.lxp.content.course.application.port.provider.view.CourseView;
import com.lxp.content.course.application.projection.CourseReadModel;
import com.lxp.content.course.application.projection.repository.CourseReadRepository;
import com.lxp.content.course.infra.persistence.mysql.read.mapper.CourseReadEntityMapper;
import com.lxp.content.course.infra.persistence.mysql.read.repository.CourseReadJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CourseReadPersistenceAdapter implements CourseReadRepository {
    private final CourseReadJpaRepository courseReadJpaRepository;
    private final CourseReadEntityMapper courseReadMapper;


    @Override
    public Page<CourseReadModel> search(String keyword, PageRequest pageRequest) {
        return PageConverter.toDomainPage(
                courseReadJpaRepository.searchByKeyword(
                        keyword,
                        PageConverter.toSpringPageable(pageRequest)
                )
        ).map(courseReadMapper::toDomain);
    }

    @Override
    public void save(CourseReadModel course) {
        courseReadJpaRepository.save(courseReadMapper.toEntity(course));
    }

    @Override
    public Boolean existsById(String courseId) {
        return courseReadJpaRepository.existsById(courseId);
    }

    @Override
    public List<CourseReadModel> filter(List<String> ids, List<String> difficulties, int count) {
        List<String> idParam = nullIfEmpty(ids);
        List<String> diffParam = nullIfEmpty(difficulties);

        return courseReadJpaRepository.filterCourses(
                        idParam,
                        diffParam,
                        org.springframework.data.domain.PageRequest.of(0, count)
                ).stream()
                .map(courseReadMapper::toDomain)
                .toList();

    }


    @Override
    public Optional<CourseReadModel> findById(String courseId) {
        return courseReadJpaRepository.findById(courseId)
                .map(courseReadMapper::toDomain);
    }

    @Override
    public Page<CourseReadModel> findAll(PageRequest pageRequest) {
        return PageConverter.toDomainPage(
                courseReadJpaRepository.findAll(PageConverter.toSpringPageable(pageRequest))
        ).map(courseReadMapper::toDomain);

    }

    private <T> List<T> nullIfEmpty(List<T> list) {
        return (list == null || list.isEmpty()) ? null : list;
    }
}
