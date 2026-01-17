package com.lxp.content.progress.application.service;

import com.lxp.content.progress.application.port.in.command.CreateProgressCommand;
import com.lxp.content.progress.application.port.out.CourseInfoLoadPort;
import com.lxp.content.progress.application.port.out.LoadCourseProgressPort;
import com.lxp.content.progress.application.port.out.SaveCourseProgressPort;
import com.lxp.content.progress.application.port.out.dto.CourseLectureInfo;
import com.lxp.content.progress.domain.model.CourseProgress;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCourseProgressServiceTest {

    @InjectMocks
    private CreateCourseProgressService createCourseProgressService;

    @Mock
    private CourseInfoLoadPort courseInfoLoadPort;
    @Mock
    private LoadCourseProgressPort loadCourseProgressPort;
    @Mock
    private SaveCourseProgressPort saveCourseProgressPort;

    @Test
    @DisplayName("TC-CCPS-001: 수강 신청 시 강좌 진행 정보가 성공적으로 초기화되어야 한다")
    void shouldCreateProgressSuccessfully() {
        // given
        String userId = UUID.randomUUID().toString();
        String courseId = UUID.randomUUID().toString();
        CreateProgressCommand command = new CreateProgressCommand(userId, courseId);

        // 멱등성 체크
        when(loadCourseProgressPort.findByUserIdAndCourseId(any(), any())).thenReturn(Optional.empty());

        List<CourseLectureInfo.LectureInfo> lectures = List.of(
                new CourseLectureInfo.LectureInfo(UUID.randomUUID().toString(), 10),
                new CourseLectureInfo.LectureInfo(UUID.randomUUID().toString(), 20)
        );
        when(courseInfoLoadPort.loadLecturesByCourseId(courseId)).thenReturn(new CourseLectureInfo(courseId, lectures));

        // when
        createCourseProgressService.execute(command);

        // then
        verify(saveCourseProgressPort, times(1)).save(any(CourseProgress.class));
    }

    @Test
    @DisplayName("TC-CCPS-002: 이미 진행 정보가 존재하면 추가로 생성하지 않는다 (멱등성 보장)")
    void shouldNotCreateWhenAlreadyExists() {
        // given
        CreateProgressCommand command = new CreateProgressCommand("user-1", "course-1");
        when(loadCourseProgressPort.findByUserIdAndCourseId(any(), any()))
                .thenReturn(Optional.of(mock(CourseProgress.class)));

        // when
        createCourseProgressService.execute(command);

        // then
        verify(courseInfoLoadPort, never()).loadLecturesByCourseId(anyString());
        verify(saveCourseProgressPort, never()).save(any());
    }
}