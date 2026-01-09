package com.lxp.content.progress.domain.policy;

import com.lxp.content.progress.domain.model.LectureProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("DefaultCalculatePolicy 단위 테스트")
class DefaultCalculatePolicyTest {

    private final DefaultCalculatePolicy calculatePolicy = new DefaultCalculatePolicy();

    private LectureProgress lp1, lp2, lp3;

    @BeforeEach
    void setUp() {
        lp1 = mock(LectureProgress.class);
        lp2 = mock(LectureProgress.class);
        lp3 = mock(LectureProgress.class);
    }

    @Test
    @DisplayName("TC-DCP-001: 빈 강의 리스트가 주어지면 진행률은 0이어야 한다")
    void shouldReturnZeroProgress_WhenLectureListIsEmpty() {
        // when
        CourseCompletionResult result = calculatePolicy.calculateCourseProgress(List.of());

        // then
        assertEquals(0.0f, result.totalProgress(), "빈 강의의 진행률은 0.0f여야 한다");
        assertFalse(result.isCompleted(), "빈 강의의 완료 여부는 false여야 한다");
    }

    @Test
    @DisplayName("TC-DCP-002: 일부 강의가 완료 되었을 경우 진행률이 올바르게 계산되어야 한다")
    void shouldCalculateCorrectProgress_WhenSomeLecturesAreCompleted() {
        // given
        when(lp1.completed()).thenReturn(true); // 완료된 강의
        when(lp2.completed()).thenReturn(true);
        when(lp3.completed()).thenReturn(false); // 완료되지 않은 강의

        // when
        CourseCompletionResult result = calculatePolicy.calculateCourseProgress(List.of(lp1, lp2, lp3));

        // then
        assertEquals(67.0f, result.totalProgress(), "일부 강의 수강 완료 시 진행률은 올바르게 계산되어야 한다");
        assertFalse(result.isCompleted(), "일부 강의 수강 완료 시 전체 완료 여부는 false여야 한다");
    }

    @Test
    @DisplayName("TC-DCP-003: 모든 강의가 완료 되었을 경우 진행률이 100, 완료상태 이어야 한다")
    void shouldReturnFullProgressAndCompleted_WhenAllLecturesAreCompleted() {
        // given
        List<LectureProgress> lectureProgresses = List.of(lp1, lp2, lp3);
        lectureProgresses.forEach(lp -> when(lp.completed()).thenReturn(true));

        // when
        CourseCompletionResult result = calculatePolicy.calculateCourseProgress(lectureProgresses);

        // then
        assertEquals(100.0f, result.totalProgress(), "모든 강의 수강 완료 시 진행률은 100.0f여야 한다");
        assertTrue(result.isCompleted(), "모든 강의 수강 완료 시 전체 완료 여부는 true여야 한다");
    }

}