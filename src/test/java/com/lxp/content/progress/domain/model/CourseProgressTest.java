package com.lxp.content.progress.domain.model;

import com.lxp.content.progress.domain.model.enums.CourseProgressStatus;
import com.lxp.content.progress.domain.policy.CourseCompletionResult;
import com.lxp.content.progress.domain.model.vo.CourseId;
import com.lxp.content.progress.domain.model.vo.LectureId;
import com.lxp.content.progress.domain.model.vo.UserId;
import com.lxp.content.progress.domain.policy.CalculatePolicy;
import com.lxp.content.progress.domain.policy.CompletionPolicy;
import com.lxp.content.progress.domain.policy.DefaultCalculatePolicy;
import com.lxp.content.progress.domain.policy.DefaultCompletionPolicy;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CourseProgress 도메인 단위 테스트")
class CourseProgressTest {

    private UserId userId;
    private CourseId courseId;
    private LectureId lectureId1, lectureId2, lectureId3;
    private List<LectureProgress> lectureProgresses;
    private CourseProgress courseProgress;
    private CompletionPolicy completionPolicy;
    private CalculatePolicy calculatePolicy;

    @BeforeEach
    void setUp() {
        userId = new UserId(UUID.randomUUID().toString());
        courseId = new CourseId(UUID.randomUUID().toString());
        lectureId1 = new LectureId(UUID.randomUUID().toString());
        lectureId2 = new LectureId(UUID.randomUUID().toString());
        lectureId3 = new LectureId(UUID.randomUUID().toString());
        lectureProgresses = List.of(
                LectureProgress.create(userId, lectureId1, 600),
                LectureProgress.create(userId, lectureId2, 900),
                LectureProgress.create(userId, lectureId3, 700)
        );

        courseProgress = CourseProgress.create(userId, courseId, lectureProgresses);
        completionPolicy = new DefaultCompletionPolicy();
        calculatePolicy = new DefaultCalculatePolicy();
    }

    @Nested
    @DisplayName("TC-CP-001: 진척도 업데이트 및 계산 테스트")
    class CalculationTest {

        private final CompletionPolicy completionPolicy = new DefaultCompletionPolicy();
        private final CalculatePolicy calculatePolicy = new DefaultCalculatePolicy();

        @Test
        @DisplayName("TC-CP-001-1: 강의 하나를 완료하면 전체 진척도가 올바르게 계산되어야 한다 (33% 버림 처리)")
        void shouldCalculateProgress_WhenOneLectureIsCompleted() {
            // given: 3개 중 1개 완료
            courseProgress.updateLectureProgress(lectureId1, 600, completionPolicy);

            // when: 정책을 통한 진척도 계산
            CourseCompletionResult result = calculatePolicy.calculateCourseProgress(courseProgress.lectureProgresses());
            courseProgress.reflectCalculation(result);

            // then: 소수점 버림(FLOOR) 결과 확인
            assertEquals(33.0f,courseProgress.totalProgress(), "전체 진척도는 33.0f여야 한다");
            assertEquals(CourseProgressStatus.IN_PROGRESS, courseProgress.studyStatus(), "강좌 상태는 IN_PROGRESS여야 한다");
        }

        @Test
        @DisplayName("TC-CP-001-2: 모든 강의를 완료하면 강좌 상태가 COMPLETED로 변경되고 완료 시간이 기록된다")
        void shouldCompleteCourse_WhenAllLecturesAreCompleted() {
            // given: 모든 강의 완료
            courseProgress.updateLectureProgress(lectureId1, 600, completionPolicy);
            courseProgress.updateLectureProgress(lectureId2, 900, completionPolicy);
            courseProgress.updateLectureProgress(lectureId3, 700, completionPolicy);

            // when: 모든 업데이트 후 정책 적용
            CourseCompletionResult result = calculatePolicy.calculateCourseProgress(courseProgress.lectureProgresses());
            courseProgress.reflectCalculation(result);

            // then
            assertAll(
                () -> assertEquals(100.0f, courseProgress.totalProgress(), "전체 진척도는 100.0f여야 한다"),
                () -> assertEquals(CourseProgressStatus.COMPLETED, courseProgress.studyStatus(), "강좌 상태는 COMPLETED여야 한다"),
                () -> assertNotNull(courseProgress.completedAt(), "완료 시간은 null이 아니어야 한다"),
                () -> assertTrue(courseProgress.isCompleted(), "isCompleted()는 true를 반환해야 한다")
            );
        }
    }

    @Nested
    @DisplayName("TC-CP-002: 예외 상황 테스트")
    class ExceptionTest {

        @Test
        @Disabled("강좌가 업데이트 되기 때문에 완료된 강좌라도 계산이 될 수 있다(완료 상태는 바뀌지 않음)")
        @DisplayName("TC-CP-002-1: 이미 완료된 강좌의 진척도를 업데이트하려 하면 예외가 발생한다")
        void shouldThrowException_WhenUpdateProgressOnCompletedCourse() {
            // given: 강좌 완료 상태 만들기
            courseProgress.updateLectureProgress(lectureId1, 600, completionPolicy);
            courseProgress.updateLectureProgress(lectureId2, 900, completionPolicy);
            courseProgress.updateLectureProgress(lectureId3, 700, completionPolicy);

            // when & then
            assertThrows(IllegalStateException.class, () -> courseProgress.updateLectureProgress(lectureId1, 100, completionPolicy),
                    "완료 상태의 강의는 진도를 업데이트 할 수 없습니다.");
        }

        @Test
        @DisplayName("TC-CP-002-2: 존재하지 않는 강의 ID로 업데이트를 시도하면 예외가 발생한다")
        void shouldThrowException_WhenLectureIdNotFound() {
            // given
            CourseProgress courseProgress = CourseProgress.create(userId, courseId, lectureProgresses);
            LectureId unknownId = new LectureId("unknown");

            // when & then
            assertThrows(IllegalArgumentException.class, () -> courseProgress.updateLectureProgress(unknownId, 50, completionPolicy),
                    "해당 LectureProgressID에 해당하는 LectureProgress가 없습니다.");
        }
    }

}