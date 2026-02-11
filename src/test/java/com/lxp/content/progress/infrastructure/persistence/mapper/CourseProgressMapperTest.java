package com.lxp.content.progress.infrastructure.persistence.mapper;

import com.lxp.content.progress.domain.model.CourseProgress;
import com.lxp.content.progress.domain.model.LectureProgress;
import com.lxp.content.progress.domain.model.enums.CourseProgressStatus;
import com.lxp.content.progress.domain.model.enums.LectureProgressStatus;
import com.lxp.content.progress.domain.model.vo.*;
import com.lxp.content.progress.infrastructure.persistence.entity.CourseProgressJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CourseProgressMapper 테스트")
class CourseProgressMapperTest {

    private final LectureProgressMapper lectureMapper = new LectureProgressMapper();
    private final CourseProgressMapper courseMapper = new CourseProgressMapper(lectureMapper);

    private String testCpId;
    private String testUserId;
    private String testCourseId;
    private String testLpId1;
    private String testLpId2;
    private CourseProgress testDomain;
    private LectureProgress testLecture1, testLecture2;
    private CourseProgressJpaEntity testEntity;
    private LocalDateTime now;

    private String randomId() {
        return UUID.randomUUID().toString();
    }

    @BeforeEach
    void setUp() {
        testCpId = randomId();
        testUserId = randomId();
        testCourseId = randomId();
        testLpId1 = randomId();
        testLpId2 = randomId();

        testLecture1 = LectureProgress.create(
                LectureProgressId.create(testLpId1),
                new UserId("u1"),
                new LectureId("l1"),
                LectureProgressStatus.IN_PROGRESS,
                100, 200);
        testLecture2 = LectureProgress.create(
                LectureProgressId.create(testLpId2),
                new UserId("u1"),
                new LectureId("l2"),
                LectureProgressStatus.COMPLETED,
                200, 200);

        testDomain = CourseProgress.create(
                CourseProgressId.create(testCpId),
                new UserId(testUserId),
                new CourseId(testCourseId),
                50.0f,
                CourseProgressStatus.IN_PROGRESS,
                null,
                List.of(testLecture1, testLecture2)
        );

        now = LocalDateTime.now();
        testEntity = CourseProgressJpaEntity.builder()
                .id(10L)
                .businessId(testCpId)
                .userId(testUserId)
                .courseId(testCourseId)
                .totalProgress(50.0f)
                .progressStatus(CourseProgressStatus.IN_PROGRESS)
                .completedAt(now)
                .build();

        testEntity.addLectureProgress(lectureMapper.toEntity(testLecture1));
        testEntity.addLectureProgress(lectureMapper.toEntity(testLecture2));
    }

    @Test
    @DisplayName("TC-CPM-001: CourseProgress 도메인을 엔티티로 변환할 때 모든 필드가 매핑 되어야 한다")
    void shouldMapAllField_WhenChangeDomainToEntity() {
        // when
        CourseProgressJpaEntity entity = courseMapper.toEntity(testDomain);

        // then
        assertAll("CourseProgress 엔티티 매핑 검증",
                () -> assertEquals(testCpId, entity.getBusinessId(), "진행률 ID가 매핑되어야 한다"),
                () -> assertEquals(testUserId, entity.getUserId(), "사용자 ID가 매핑되어야 한다"),
                () -> assertEquals(testCourseId, entity.getCourseId(), "강좌 ID가 매핑되어야 한다"),
                () -> assertEquals(50.0f, entity.getTotalProgress(), "총 진행률이 매핑되어야 한다"),
                () -> assertEquals(CourseProgressStatus.IN_PROGRESS, entity.getProgressStatus(), "진행 상태가 매핑되어야 한다"),
                () -> assertEquals(2, entity.getLectureProgresses().size(), "자식 강의 진행률 개수가 일치해야 한다"),

                // 자식 엔티티의 연관관계(부모 참조) 검증을 그룹화
                () -> assertAll("자식 강의의 부모 참조 검증",
                        () -> assertEquals(entity, entity.getLectureProgresses().get(0).getCourseProgress(), "첫 번째 자식의 부모 참조가 올바르지 않음"),
                        () -> assertEquals(entity, entity.getLectureProgresses().get(1).getCourseProgress(), "두 번째 자식의 부모 참조가 올바르지 않음")
                )
        );
    }

    @Test
    @DisplayName("TC-CPM-002: 엔티티에서 도메인으로 복구 시 모든 필드가 정확히 매핑된다")
    void shouldMapAllField_WhenChangeEntityToDomain() {
        // when
        CourseProgress domain = courseMapper.toDomain(testEntity);

        // then
        assertAll("엔티티 -> 도메인 매핑 검증",
                () -> assertEquals(testEntity.getBusinessId(), domain.getId().value(), "ID가 일치해야 합니다"),
                () -> assertEquals(CourseProgressStatus.IN_PROGRESS, domain.studyStatus(), "상태가 일치해야 합니다"),
                () -> assertEquals(now, domain.completedAt(), "완료 시간이 일치해야 합니다"),
                () -> assertEquals(50.0f, domain.totalProgress(), "진행률이 일치해야 합니다")
        );
    }

}