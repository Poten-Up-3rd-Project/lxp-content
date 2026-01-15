package com.lxp.content.progress.application.service;

import com.lxp.content.progress.application.mapper.ProgressWebMapper;
import com.lxp.content.progress.application.port.in.query.GetActiveCourseProgressQuery;
import com.lxp.content.progress.application.port.in.response.CourseProgressInfo;
import com.lxp.content.progress.application.port.out.LoadCourseProgressPort;
import com.lxp.content.progress.domain.model.CourseProgress;
import com.lxp.content.progress.domain.model.LectureProgress;
import com.lxp.content.progress.domain.model.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.description;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetActiveCourseProgressService 테스트")
class GetActiveCourseProgressServiceTest {

    @Mock
    private LoadCourseProgressPort loadPort;

    @Mock
    private ProgressWebMapper mapper;

    @InjectMocks
    private GetActiveCourseProgressService service;

    private List<CourseProgress> mockCourseProgressList;
    private UserId userId;

    private final static int COURSE_COUNT = 3;

    @BeforeEach
    void setUp() {
        userId = new UserId(UUID.randomUUID().toString());
        mockCourseProgressList = new ArrayList<>();

        // 데이터 셋업 (기존 코드를 정돈하여 리스트에 담음)
        for (int i = 0; i < COURSE_COUNT; i++) {
            CourseId courseId = new CourseId(UUID.randomUUID().toString());
            List<LectureProgress> lectures = List.of(
                    LectureProgress.create(userId, new LectureId(UUID.randomUUID().toString()), 300)
            );
            mockCourseProgressList.add(CourseProgress.create(userId, courseId, lectures));
        }
    }

    @Test
    @DisplayName("TC-GACPS-001: 사용자 ID 기반의 강좌들의 진행률이 정상적으로 조회되어야 한다")
    void shouldReturnCorrectCourseProgressList_WhenFindItWithUserId() {
        // given
        GetActiveCourseProgressQuery query = new GetActiveCourseProgressQuery(userId.value());
        List<CourseProgressInfo> expectedResponse = List.of(
                new CourseProgressInfo(
                        mockCourseProgressList.get(0).courseId().value(),
                        0,
                        false),
                new CourseProgressInfo(
                        mockCourseProgressList.get(1).courseId().value(),
                        0,
                        false),
                new CourseProgressInfo(
                        mockCourseProgressList.get(2).courseId().value(),
                        0,
                        false)
        );

        given(loadPort.findByUserId(userId.value())).willReturn(mockCourseProgressList);
        given(mapper.toResponseListAsCourseProgressInfo(mockCourseProgressList)).willReturn(expectedResponse);

        // when
        List<CourseProgressInfo> result = service.execute(query);

        // then
        assertEquals(expectedResponse, result, "조회된 강좌 진행률 리스트가 예상한 결과와 일치해야 한다");
        verify(loadPort, description("사용자 ID 기반으로 강좌 정보가 조회되어야 합니다")).findByUserId(userId.value());
        verify(mapper, description("Mapper에 의해 CourseProgressInfo의 리스트로 변환되어야 합니다")).toResponseListAsCourseProgressInfo(anyList());
    }

    @Test
    @DisplayName("TC-GACPS-002: 수강 중인 강좌가 없는 경우 빈 리스트를 반환해야 한다")
    void shouldReturnEmptyListWhenNoActiveCourse() {
        // given
        GetActiveCourseProgressQuery query = new GetActiveCourseProgressQuery(userId.value());
        given(loadPort.findByUserId(userId.value())).willReturn(List.of());
        given(mapper.toResponseListAsCourseProgressInfo(List.of())).willReturn(List.of());

        // when
        List<CourseProgressInfo> result = service.execute(query);

        // then
        assertEquals(List.of(), result, "수강 중인 강좌가 없을 경우 빈 리스트를 반환해야 한다");
        verify(loadPort, description("사용자 ID 기반으로 강좌 정보가 조회되어야 합니다")).findByUserId(userId.value());
    }

    @Test
    @DisplayName("TC-GACPS-003: 포트에서 예외가 발생할 경우 서비스도 예외를 전파해야 한다")
    void shouldPropagateExceptionWhenPortFails() {
        // given
        GetActiveCourseProgressQuery query = new GetActiveCourseProgressQuery(userId.value());
        given(loadPort.findByUserId(userId.value())).willThrow(new RuntimeException("DB 연결 실패"));

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            service.execute(query);
        }, "포트에서 예외가 발생할 경우 서비스도 동일한 예외를 던져야 한다");
    }
}