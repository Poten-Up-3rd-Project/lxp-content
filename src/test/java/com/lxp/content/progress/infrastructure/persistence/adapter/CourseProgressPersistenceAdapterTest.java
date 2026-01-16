package com.lxp.content.progress.infrastructure.persistence.adapter;

import com.lxp.content.progress.domain.model.CourseProgress;
import com.lxp.content.progress.domain.model.LectureProgress;
import com.lxp.content.progress.domain.model.enums.CourseProgressStatus;
import com.lxp.content.progress.domain.model.enums.LectureProgressStatus;
import com.lxp.content.progress.domain.model.vo.*;
import com.lxp.content.progress.infrastructure.persistence.entity.CourseProgressJpaEntity;
import com.lxp.content.progress.infrastructure.persistence.entity.LectureProgressJpaEntity;
import com.lxp.content.progress.infrastructure.persistence.mapper.CourseProgressMapper;
import com.lxp.content.progress.infrastructure.persistence.mapper.LectureProgressMapper;
import com.lxp.content.progress.infrastructure.persistence.repository.JpaCourseProgressRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(useDefaultFilters = false)
@EntityScan(basePackageClasses = {CourseProgressJpaEntity.class})
@DisplayName("CourseProgressPersistenceAdapter 테스트")
class CourseProgressPersistenceAdapterTest {

    @TestConfiguration
    @EnableJpaRepositories(basePackageClasses = JpaCourseProgressRepository.class)
    @Import({
            CourseProgressPersistenceAdapter.class,
            CourseProgressMapper.class,
            LectureProgressMapper.class
    })
    static class TestConfig {}

    @Autowired
    private CourseProgressPersistenceAdapter adapter;

    @Autowired
    private JpaCourseProgressRepository jpaRepository;

    @Autowired
    private EntityManager em;

    private String randomId() {
        return UUID.randomUUID().toString();
    }

    @Test
    @DisplayName("TC-CPPA-001: 새로운 강좌 진행도를 저장하면 DB에 정상적으로 Insert 된다")
    void shouldInsertDatabase_WhenSaveCourseProgress() {
        // given
        String bizId = randomId();
        CourseProgress domain = CourseProgress.create(
                CourseProgressId.create(bizId),
                new UserId(randomId()),
                new CourseId(randomId()),
                0.0f,
                CourseProgressStatus.IN_PROGRESS,
                null,
                List.of()
        );

        // when
        adapter.save(domain);
        em.flush();
        em.clear();

        // then
        var entity = jpaRepository.findByBusinessIdWithLecture(bizId);
        assertNotEquals(Optional.empty(), entity, "저장된 강좌 진행도를 찾을 수 있어야 한다");
        assertEquals(bizId, entity.get().getBusinessId(), "저장된 강좌 진행도의 비즈니스 ID가 일치해야 한다");
    }

    @Test
    @DisplayName("TC-CPPA-002: 기존 진행도를 수정하면 save() 호출 없이도 변경 감지(Dirty Checking)로 업데이트 된다")
    void shouldUpdateExistingProgressUsingDirtyChecking_WhenModifyProgress() {
        // given: 초기 데이터 저장
        String bizId = randomId();
        CourseId courseId = new CourseId(randomId());
        UserId userId = new UserId(randomId());

        CourseProgress initialDomain = CourseProgress.create(
                CourseProgressId.create(bizId),
                userId,
                courseId,
                10.0f,
                CourseProgressStatus.IN_PROGRESS,
                null,
                List.of()
        );
        adapter.save(initialDomain);
        em.flush();
        em.clear();

        // when: 도메인 모델의 상태를 변경하여 다시 저장 시도
        CourseProgress updatedDomain = CourseProgress.create(
                CourseProgressId.create(bizId),
                userId,
                courseId,
                100.0f, // 진행도 변경
                CourseProgressStatus.COMPLETED, // 상태 변경
                null,
                List.of()
        );
        adapter.save(updatedDomain);
        em.flush();
        em.clear();

        // then: DB에 반영되었는지 확인
        CourseProgressJpaEntity entity = jpaRepository.findByBusinessIdWithLecture(bizId).orElseThrow();
        assertEquals(100.0f, entity.getTotalProgress(), "진행도가 업데이트되어야 한다");
        assertEquals(CourseProgressStatus.COMPLETED, entity.getProgressStatus(), "진행 완료 시 진행 상태가 COMPLETED로 업데이트되어야 한다");
    }

    @Test
    @DisplayName("TC-CPPA-003: 자식인 LectureProgress를 추가하면 Cascade에 의해 함께 저장된다")
    void shouldSaveChildrenTogetherWithCascade_WhenAddLectureProgress() {
        // given
        String bizId = randomId();
        String lecId = randomId();
        String lpBizId = randomId();
        LectureProgress lecture = LectureProgress.create(
            LectureProgressId.create(lpBizId),
            new UserId("u1"),
            new LectureId(lecId),
            LectureProgressStatus.NOT_STARTED,
            1000,
                2000
        );

        CourseProgress domain = CourseProgress.create(
                CourseProgressId.create(bizId),
                new UserId("u1"),
                new CourseId(randomId()),
                50.0f,
                CourseProgressStatus.IN_PROGRESS,
                null,
                List.of(lecture)
        );

        // when
        adapter.save(domain);
        em.flush();
        em.clear();

        // then
        CourseProgressJpaEntity entity = jpaRepository.findByBusinessIdWithLecture(bizId).orElseThrow();
        assertEquals(1, entity.getLectureProgresses().size(), "자식 강의 진행률이 함께 저장되어야 한다");
        assertEquals(lecId, entity.getLectureProgresses().get(0).getLectureId(), "저장된 자식 강의 진행률의 강의 ID가 일치해야 한다");
        assertNotNull(entity.getLectureProgresses().get(0).getCourseProgress(), "자식 강의 진행률의 부모 참조가 올바르게 설정되어야 한다");
    }

    @Test
    @DisplayName("TC-CPPA-004: 부모(CourseProgress)를 삭제하면 자식(LectureProgress)들도 함께 삭제된다")
    void shouldDeleteChildren_WhenParentIsDeleted() {
        // given: 부모와 자식이 포함된 데이터 저장
        String bizId = randomId();
        String lecId = randomId();
        String lpBizId = randomId();
        LectureProgress lecture = LectureProgress.create(
                LectureProgressId.create(lpBizId),
                new UserId("u1"),
                new LectureId(lecId),
                LectureProgressStatus.NOT_STARTED,
                1000,
                2000
        );
        CourseProgress domain = CourseProgress.create(
                CourseProgressId.create(bizId),
                new UserId("u1"),
                new CourseId(randomId()),
                0.0f,
                CourseProgressStatus.IN_PROGRESS,
                null,
                List.of(lecture)
        );
        adapter.save(domain);
        em.flush();
        em.clear();

        // 저장 확인
        CourseProgressJpaEntity savedParent = jpaRepository.findByBusinessIdWithLecture(bizId).orElseThrow();
        Long childId = savedParent.getLectureProgresses().get(0).getId();

        // when: 부모 삭제
        jpaRepository.delete(savedParent);
        em.flush();
        em.clear();

        // then: 부모와 자식 모두 조회되지 않아야 함
        assertEquals(Optional.empty(), jpaRepository.findByBusinessIdWithLecture(bizId), "부모 강좌 진행도가 삭제되어야 한다");
        // EntityManager를 통해 자식 엔티티가 삭제되었는지 직접 확인
        assertNull(em.find(LectureProgressJpaEntity.class, childId), "자식 강의 진행률도 삭제되어야 한다");
    }

    @Test
    @DisplayName("TC-CPPA-005: 부모의 자식 리스트에서 특정 항목을 제거하면 DB에서도 해당 자식이 삭제된다 (Orphan Removal)")
    void shouldDeleteOrphan_WhenRemovedFromList() {
        // given: 자식이 2개인 상태로 저장
        String bizId = randomId();
        String lecPgId1 = randomId();
        String lecPgId2 = randomId();
        String lecId1 = randomId();
        String lecId2 = randomId();
        UserId userId = new UserId("u1");
        LectureProgress lec1 = LectureProgress.create(
                LectureProgressId.create(lecPgId1),
                new UserId("u1"),
                new LectureId(lecId1),
                LectureProgressStatus.NOT_STARTED,
                700,
                1800
        );
        LectureProgress lec2 = LectureProgress.create(
                LectureProgressId.create(lecPgId2),
                new UserId("u1"),
                new LectureId(lecId2),
                LectureProgressStatus.NOT_STARTED,
                1000,
                2000
        );

        CourseProgress domain = CourseProgress.create(
                CourseProgressId.create(bizId),
                userId,
                new CourseId(randomId()),
                50.0f,
                CourseProgressStatus.IN_PROGRESS,
                null,
                List.of(lec1, lec2)
        );
        adapter.save(domain);
        em.flush();
        em.clear();

        // when: 도메인 모델에서 자식 하나를 제외하고 다시 저장 (업데이트)
        CourseProgress updatedDomain = CourseProgress.create(
                CourseProgressId.create(bizId),
                userId,
                domain.courseId(),
                50.0f,
                CourseProgressStatus.IN_PROGRESS,
                null,
                List.of(lec1) // lec2 제거
        );
        adapter.save(updatedDomain);
        em.flush();
        em.clear();

        // then: DB에 자식이 1개만 남아야 함
        CourseProgressJpaEntity entity = jpaRepository.findByBusinessIdWithLecture(bizId).orElseThrow();
        assertEquals(1, entity.getLectureProgresses().size(), "자식 강의 진행률이 1개만 남아야 한다");
    }

}