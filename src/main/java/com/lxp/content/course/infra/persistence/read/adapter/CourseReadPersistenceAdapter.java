package com.lxp.content.course.infra.persistence.read.adapter;

import com.lxp.common.domain.pagination.Page;
import com.lxp.common.domain.pagination.PageRequest;
import com.lxp.common.infrastructure.persistence.PageConverter;
import com.lxp.content.course.application.projection.CourseReadModel;
import com.lxp.content.course.application.projection.repository.CourseReadRepository;
import com.lxp.content.course.infra.persistence.read.mapper.CourseReadEntityMapper;
import com.lxp.content.course.infra.persistence.read.repository.CourseReadJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
    public Optional<CourseReadModel> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Page<CourseReadModel> findAll(PageRequest pageRequest) {
        return PageConverter.toDomainPage(
                courseReadJpaRepository.findAll(PageConverter.toSpringPageable(pageRequest))
        ).map(courseReadMapper::toDomain);

    }
}
