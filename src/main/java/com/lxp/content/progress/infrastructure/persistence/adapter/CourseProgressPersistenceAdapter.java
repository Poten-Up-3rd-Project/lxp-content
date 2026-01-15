package com.lxp.content.progress.infrastructure.persistence.adapter;

import com.lxp.content.progress.application.port.out.LoadCourseProgressPort;
import com.lxp.content.progress.application.port.out.SaveCourseProgressPort;
import com.lxp.content.progress.domain.model.CourseProgress;
import com.lxp.content.progress.domain.model.vo.*;
import com.lxp.content.progress.infrastructure.persistence.entity.CourseProgressJpaEntity;
import com.lxp.content.progress.infrastructure.persistence.mapper.CourseProgressMapper;
import com.lxp.content.progress.infrastructure.persistence.repository.JpaCourseProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 강좌 진행률 영속성 어댑터
 */
@Component
@RequiredArgsConstructor
public class CourseProgressPersistenceAdapter implements SaveCourseProgressPort, LoadCourseProgressPort {

    private final JpaCourseProgressRepository jpaRepository;
    private final CourseProgressMapper courseProgressMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CourseProgress> findByUserId(String id) {
        List<CourseProgressJpaEntity> progresses = jpaRepository.findByUserIdWithLecture(id);

        return progresses.stream()
                .map(courseProgressMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CourseProgress> findByUserIdAndCourseId(String userId, String courseId) {
        return jpaRepository.findByUserIdAndCourseIdWithLectures(userId, courseId)
                .map(courseProgressMapper::toDomain);
    }

    @Override
    @Transactional
    public void save(CourseProgress domain) {
        jpaRepository.findByBusinessIdWithLecture(domain.getId().value())
            .ifPresentOrElse(
                existingEntity -> courseProgressMapper.updateEntity(domain, existingEntity),
                () -> jpaRepository.save(courseProgressMapper.toEntity(domain))
            );
    }
}
