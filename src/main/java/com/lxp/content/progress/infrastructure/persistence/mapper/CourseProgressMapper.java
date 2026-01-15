package com.lxp.content.progress.infrastructure.persistence.mapper;

import com.lxp.common.application.port.out.DomainMapper;
import com.lxp.content.progress.domain.model.CourseProgress;
import com.lxp.content.progress.domain.model.LectureProgress;
import com.lxp.content.progress.domain.model.vo.CourseId;
import com.lxp.content.progress.domain.model.vo.CourseProgressId;
import com.lxp.content.progress.domain.model.vo.UserId;
import com.lxp.content.progress.infrastructure.persistence.entity.CourseProgressJpaEntity;
import com.lxp.content.progress.infrastructure.persistence.entity.LectureProgressJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 강좌 진행률 도메인 <-> 엔티티 매퍼
 */
@Component
public class CourseProgressMapper implements DomainMapper<CourseProgress, CourseProgressJpaEntity> {

    private final LectureProgressMapper lectureProgressMapper;

    public CourseProgressMapper(LectureProgressMapper lectureProgressMapper) {
        this.lectureProgressMapper = lectureProgressMapper;
    }

    /**
     * 도메인 -> 엔티티 변환
     * @param domain 강좌 진행률 도메인
     * @return 강좌 진행률 엔티티
     */
    @Override
    public CourseProgressJpaEntity toEntity(CourseProgress domain) {
        if (domain == null) return null;

        CourseProgressJpaEntity entity = CourseProgressJpaEntity.builder()
                .businessId(domain.getId().value())
                .userId(domain.userId().value())
                .courseId(domain.courseId().value())
                .totalProgress(domain.totalProgress())
                .progressStatus(domain.studyStatus())
                .completedAt(domain.completedAt())
                .build();

        // 양방향 연관관계 및 자식 엔티티 매핑
        if (domain.lectureProgresses() != null) {
            for (LectureProgress lectureDomain : domain.lectureProgresses()) {
                LectureProgressJpaEntity lectureEntity = lectureProgressMapper.toEntity(lectureDomain);
                entity.addLectureProgress(lectureEntity);
            }
        }

        return entity;
    }

    /**
     * 엔티티 -> 도메인 변환
     * @param entity 강좌 진행률 엔티티
     * @return 강좌 진행률 도메인
     */
    @Override
    public CourseProgress toDomain(CourseProgressJpaEntity entity) {
        if (entity == null) return null;

        List<LectureProgress> lectureDomains = entity.getLectureProgresses().stream()
                .map(lectureProgressMapper::toDomain)
                .collect(Collectors.toList());

        return CourseProgress.create(
                CourseProgressId.create(entity.getBusinessId()),
                new UserId(entity.getUserId()),
                new CourseId(entity.getCourseId()),
                entity.getTotalProgress(),
                entity.getProgressStatus(),
                entity.getCompletedAt(),
                lectureDomains
        );
    }

    /**
     * Domain -> Entity 업데이트
     * @param domain 강좌 진행률 도메인
     * @param entity 강좌 진행률 엔티티
     */
    public void updateEntity(CourseProgress domain, CourseProgressJpaEntity entity) {
        if (domain == null || entity == null) return;

        entity.updateFromDomain(
                domain.totalProgress(),
                domain.studyStatus(),
                domain.completedAt()
        );

        syncLectureProgresses(domain.lectureProgresses(), entity);
    }

    /**
     * 강의 진행률 동기화
     * 삭제 된 강의 삭제 후
     * 변경된 강의 진행률은 업데이트, 신규 강의 진행률은 추가
     * @param domainList 강의 진행률 도메인 리스트
     * @param entity 강좌 진행률 엔티티
     */
    private void syncLectureProgresses(List<LectureProgress> domainList, CourseProgressJpaEntity entity) {
        Set<String> domainIds = domainList.stream()
                        .map(lp -> lp.getId().value())
                        .collect(Collectors.toSet());

        entity.getLectureProgresses().removeIf(entityLp ->
                !domainIds.contains(entityLp.getLectureId())
        );

        domainList.forEach(domainLp -> {
            entity.getLectureProgresses().stream()
                .filter(entityLp -> entityLp.getLectureId().equals(domainLp.getId().value()))
                .findFirst()
                .ifPresentOrElse(
                    existingLp -> lectureProgressMapper.updateEntity(domainLp, existingLp),
                    () -> entity.addLectureProgress(lectureProgressMapper.toEntity(domainLp))
                );
        });
    }

}
