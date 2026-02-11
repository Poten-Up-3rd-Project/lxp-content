package com.lxp.content.progress.infrastructure.persistence.mapper;

import com.lxp.content.progress.domain.model.LectureProgress;
import com.lxp.content.progress.domain.model.enums.LectureProgressStatus;
import com.lxp.content.progress.domain.model.vo.LectureId;
import com.lxp.content.progress.domain.model.vo.LectureProgressId;
import com.lxp.content.progress.domain.model.vo.UserId;
import com.lxp.content.progress.infrastructure.persistence.entity.LectureProgressJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LectureProgressMapper 테스트")
class LectureProgressMapperTest {

    private LectureProgressMapper mapper;
    private LectureProgress testDomain;
    private LectureProgressJpaEntity testEntity;
    private String testLectureProgressId, testUserId, testLectureId;

    // 랜덤 UUID 문자열 생성을 위한 헬퍼 메서드
    private String randomId() {
        return UUID.randomUUID().toString();
    }

    @BeforeEach
    void setUp() {
        mapper = new LectureProgressMapper();

        testLectureProgressId = randomId();
        testUserId = randomId();
        testLectureId = randomId();

        // 랜덤 UUID를 가진 도메인 객체 생성
        testDomain = LectureProgress.create(
                LectureProgressId.create(testLectureProgressId),
                new UserId(testUserId),
                new LectureId(testLectureId),
                LectureProgressStatus.IN_PROGRESS,
                300,
                600
        );

        // 랜덤 UUID를 가진 엔티티 객체 생성
        testEntity = LectureProgressJpaEntity.builder()
                .id(1L) // DB Sequence ID 가정
                .businessId(testLectureProgressId)
                .userId(testUserId)
                .lectureId(testLectureId)
                .progressStatus(LectureProgressStatus.COMPLETED)
                .lastPlayedTime(500)
                .totalDuration(500)
                .build();
    }

    @Test
    @DisplayName("TC-LPM-001: 도메인 모델을 엔티티로 변환 시 모든 필드 값이 유지되어야 한다")
    void shouldMapAllField_WhenChangeDomainToEntity() {
        // when
        LectureProgressJpaEntity result = mapper.toEntity(testDomain);

        // then
        assertEquals(testLectureProgressId, result.getBusinessId());
        assertEquals(testUserId, result.getUserId());
        assertEquals(testLectureId, result.getLectureId());
        assertEquals(testDomain.lectureProgressStatus(), result.getProgressStatus());
        assertEquals(testDomain.lastPlayedTimeInSeconds(), result.getLastPlayedTime());
        assertEquals(testDomain.totalDurationInSeconds(), result.getTotalDuration());
    }

    @Test
    @DisplayName("엔티티를 도메인으로 변환 시 모든 필드 값이 VO로 올바르게 주입되어야 한다")
    void shouldInjectAllFieldAsValueObject_WhenChangeEntityToDomain() {
        // when
        LectureProgress result = mapper.toDomain(testEntity);

        // then
        assertEquals(testLectureProgressId, result.getId().value());
        assertEquals(testUserId, result.userId().value());
        assertEquals(testLectureId, result.lectureId().value());
        assertEquals(testEntity.getProgressStatus(), result.lectureProgressStatus());
        assertEquals(testEntity.getLastPlayedTime(), result.lastPlayedTimeInSeconds());
        assertEquals(testEntity.getTotalDuration(), result.totalDurationInSeconds());
    }

}