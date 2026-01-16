package com.lxp.content.progress.infrastructure.persistence.mapper;

import com.lxp.common.application.port.out.DomainMapper;
import com.lxp.content.progress.domain.model.LectureProgress;
import com.lxp.content.progress.domain.model.vo.LectureId;
import com.lxp.content.progress.domain.model.vo.LectureProgressId;
import com.lxp.content.progress.domain.model.vo.UserId;
import com.lxp.content.progress.infrastructure.persistence.entity.LectureProgressJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class LectureProgressMapper implements DomainMapper<LectureProgress, LectureProgressJpaEntity> {

    /**
     * Entity -> Domain 변환
     * @param entity 강의 진행률 엔티티
     * @return 강의 진행률 도메인
     */
    @Override
    public LectureProgress toDomain(LectureProgressJpaEntity entity) {
        return entity == null? null :
            LectureProgress.create(
                LectureProgressId.create(entity.getBusinessId()),
                new UserId(entity.getUserId()),
                new LectureId(entity.getLectureId()),
                entity.getProgressStatus(),
                entity.getLastPlayedTime(),
                entity.getTotalDuration()
            );
    }

    /**
     * Domain -> Entity 변환
     * @param domain 강의 진행률 도메인
     * @return 강의 진행률 엔티티
     */
    @Override
    public LectureProgressJpaEntity toEntity(LectureProgress domain) {
        return domain == null? null :
            LectureProgressJpaEntity.builder()
                .businessId(domain.getId().value())
                .userId(domain.userId().value())
                .lectureId(domain.lectureId().value())
                .progressStatus(domain.lectureProgressStatus())
                .lastPlayedTime(domain.lastPlayedTimeInSeconds())
                .totalDuration(domain.totalDurationInSeconds())
                .build();
    }

    /**
     * Domain -> Entity 업데이트
     * @param domain 강의 진행률 도메인
     * @param entity 강의 진행률 엔티티
     */
    public void updateEntity(LectureProgress domain, LectureProgressJpaEntity entity) {
        if (domain == null || entity == null) return;

        entity.update(
            domain.lastPlayedTimeInSeconds(),
            domain.lectureProgressStatus()
        );
    }

}
