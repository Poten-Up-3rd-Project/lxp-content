package com.lxp.content.progress.domain.model;

import com.lxp.content.progress.domain.model.enums.LectureProgressStatus;
import com.lxp.content.progress.domain.model.vo.LectureId;
import com.lxp.content.progress.domain.model.vo.UserId;
import com.lxp.content.progress.domain.policy.CompletionPolicy;
import com.lxp.content.progress.domain.policy.DefaultCompletionPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LectureProgress 도메인 단위 테스트")
class LectureProgressTest {

    private UserId userId;
    private LectureId lectureId;
    private Integer totalDurationInSeconds;
    private LectureProgress lectureProgress;

    private CompletionPolicy completionPolicy;

    @BeforeEach
    void setUp() {
        userId = new UserId(UUID.randomUUID().toString());
        lectureId = new LectureId(UUID.randomUUID().toString());

        totalDurationInSeconds = 600;

        lectureProgress = LectureProgress.create(
                userId, lectureId, totalDurationInSeconds
        );

        completionPolicy = new DefaultCompletionPolicy();
    }

    @Test
    @DisplayName("TC-LP-001: 강의 진행 생성 시 초기 상태는 'NOT_STARTED' 이며 재생 시간은 0초여야 한다")
    void shouldDefaultValues_WhenCreatingLectureProgress() {
        assertAll(
                () -> assertSame(LectureProgressStatus.NOT_STARTED, lectureProgress.lectureProgressStatus(),
                        "초기 상태의 LectureProgressStatus는 NOT_STARTED여야 한다"),
                () -> assertEquals(0, lectureProgress.lastPlayedTimeInSeconds(),
                        "초기 상태의 lastPlayedTimeInSeconds는 0이어야 한다")
        );
    }

    @Nested
    @DisplayName("TC-LP-001: 강의 진행 생성 시 유효하지 않은 입력값이 들어오면 예외가 발생한다")
    class CreationValidationTest {

        @Test
        @DisplayName("TC-LP-001-1: UserId가 null인 경우 예외가 발생해야 한다")
        void shouldThrowNullPointerException_WhenParameterIsNull() {
            assertAll(
                () -> assertThrows(NullPointerException.class, () -> LectureProgress.create(null, lectureId, 540),
                        "UserId가 null인 경우 예외가 발생해야 한다"),
                () -> assertThrows(NullPointerException.class, () -> LectureProgress.create(userId, null, 540),
                        "LectureId가 null인 경우 예외가 발생해야 한다"),
                () -> assertThrows(NullPointerException.class, () -> LectureProgress.create(userId, lectureId, null),
                        "totalDurationInSeconds가 null인 경우 예외가 발생해야 한다")
            );
        }

        @Test
        @DisplayName("전체 재생 시간이 0 이하면 IllegalArgumentException이 발생한다")
        void shouldThrowException_WhenDurationIsInvalid() {
            assertThrows(IllegalArgumentException.class,
                    () -> LectureProgress.create(userId, lectureId, 0));
        }

    }

    @Nested
    @DisplayName("TC-LP-002: updateLastPlayedTime 메서드 유효성 검사")
    class UpdateLastPlayedTimeValidationTest {

        @DisplayName("TC-LP-002-1: 재생 시간이 0 이하인 경우 예외가 발생해야 한다")
        void shouldThrowException_WhenLastPlayedTimeIsNegative() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                lectureProgress.updateLastPlayedTime(-10, completionPolicy);
            });

            assertEquals("lastPlayedTimeInSeconds는 음수일 수 없습니다.", exception.getMessage(),
                    "재생 시간이 음수인 경우 올바른 예외 메시지가 반환되어야 한다");
        }

        @Test
        @DisplayName("TC-LP-002-2: 재생 시간이 전체 재생 시간을 초과하는 경우 예외가 발생해야 한다")
        void shouldThrowException_WhenLastPlayedTimeExceedsTotalDuration() {
            Exception exception = assertThrows(IllegalArgumentException.class, () ->
                lectureProgress.updateLastPlayedTime(700, completionPolicy),
                "재생 시간이 전체 재생 시간을 초과하는 경우 올바른 예외 메시지가 반환되어야 한다");
        }
    }

    @Nested
    @DisplayName("TC-LP-003: updateLastPlayedTime 메서드 로직 테스트")
    class UpdateLastPlayedTimeLogicTest {

        @Test
        @DisplayName("TC-LP-003-1: 마지막 재생시간 입력 시 진행 중 상태로 업데이트 되어야 한다")
        void shouldUpdateToInProgressStatus_WhenUpdateLastPlayedTime() {
            // when - 진행 중 상태로 업데이트
            lectureProgress.updateLastPlayedTime(300, completionPolicy);

            // then
            assertEquals(300, lectureProgress.lastPlayedTimeInSeconds(),
                    "lastPlayedTimeInSeconds는 300이어야 한다");
            assertSame(LectureProgressStatus.IN_PROGRESS, lectureProgress.lectureProgressStatus(),
                    "LectureProgressStatus는 IN_PROGRESS여야 한다");
        }

        @Test
        @DisplayName("TC-LP-003-2: 마지막 재생시간 입력 시 완료 상태로 업데이트 되어야 한다")
        void shouldUpdateToCompletedStatus_WhenUpdateLastPlayedTime() {
            // when - 완료 상태로 업데이트
            lectureProgress.updateLastPlayedTime(600, completionPolicy);

            // then
            assertEquals(600, lectureProgress.lastPlayedTimeInSeconds(),
                    "lastPlayedTimeInSeconds는 600이어야 한다");
            assertSame(LectureProgressStatus.COMPLETED, lectureProgress.lectureProgressStatus(),
                    "LectureProgressStatus는 COMPLETED여야 한다");
        }

        @Test
        @DisplayName("TC-LP-003-3: 완료된 강의에 대해 다시 업데이트 될 수 없다")
        void shouldNotUpdate_WhenLectureProgressStatusIsCompleted() {
            // when - 완료 상태로 업데이트
            lectureProgress.updateLastPlayedTime(600, completionPolicy);

            // when - 완료된 강의에 대해 다시 업데이트 시도
            Exception exception = assertThrows(IllegalStateException.class, () -> {
                lectureProgress.updateLastPlayedTime(650, completionPolicy);
            });

            // then
            assertEquals("완료 상태의 강의는 진도를 업데이트 할 수 없습니다.", exception.getMessage(),
                    "완료된 강의에 대해 업데이트 시도 시 올바른 예외 메시지가 반환되어야 한다");
        }
    }

    @Test
    @DisplayName("TC-LP-004: completed 메서드는 LectureProgress 상태가 COMPLETED, 재생시간 == 전체시간일 때 true를 반환한다")
    void shouldReturnTrue_WhenLectureIsCompleted() {
        // when - 완료 상태로 업데이트
        lectureProgress.updateLastPlayedTime(600, completionPolicy);

        // then
        assertTrue(lectureProgress.completed(),
                "강의가 완료된 경우 completed 메서드는 true를 반환해야 한다");
    }
}