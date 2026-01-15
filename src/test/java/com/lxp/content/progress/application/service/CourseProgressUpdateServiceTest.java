package com.lxp.content.progress.application.service;

import com.lxp.content.progress.application.port.in.command.UpdateProgressCommand;
import com.lxp.content.progress.application.port.out.LoadCourseProgressPort;
import com.lxp.content.progress.application.port.out.SaveCourseProgressPort;
import com.lxp.content.progress.domain.model.CourseProgress;
import com.lxp.content.progress.domain.service.CourseProgressDomainService;
import com.lxp.content.progress.exception.ProgressDomainException;
import com.lxp.content.progress.exception.ProgressErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("강좌 진행률 업데이트 서비스 테스트")
class CourseProgressUpdateServiceTest {

    @Mock
    private LoadCourseProgressPort loadPort;

    @Mock
    private SaveCourseProgressPort savePort;

    @Mock
    private CourseProgressDomainService domainService;

    @InjectMocks
    private CourseProgressUpdateService srv; // 테스트 대상

    private String userId;
    private String courseId;
    private String lectureId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        courseId = UUID.randomUUID().toString();
        lectureId = UUID.randomUUID().toString();
    }

    @Test
    @DisplayName("TC-CPUS-001: 강의 진행도 업데이트가 정상적으로 흐름을 타는지 확인한다")
    void execute_success() {
        // given
        UpdateProgressCommand command = new UpdateProgressCommand(userId, courseId, lectureId, 100);

        CourseProgress mockProgress = mock(CourseProgress.class);
        when(loadPort.findByUserIdAndCourseId(userId, courseId))
                .thenReturn(Optional.of(mockProgress));

        // when
        srv.execute(command);

        // then
        // 도메인 서비스가 생성된 lectureId와 함께 호출되었는지 검증
        verify(domainService, times(1)).updateProcess(
                eq(mockProgress),
                argThat(id -> id.value().equals(lectureId)),
                eq(100)
        );

        verify(savePort, times(1)).save(mockProgress);
    }

    @Test
    @DisplayName("TC-CPUS-002: 수강 정보가 없을 경우 IllegalArgumentException을 던진다")
    void execute_fail_notFound() {
        // given
        UpdateProgressCommand command = new UpdateProgressCommand(userId, courseId, lectureId, 0);
        when(loadPort.findByUserIdAndCourseId(userId, courseId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ProgressDomainException.class, () -> srv.execute(command));
        assertEquals(ProgressErrorCode.COURSE_PROGRESS_NOT_FOUND.getCode(),
                assertThrows(ProgressDomainException.class, () -> srv.execute(command)).getErrorCode().getCode());

        // 저장은 호출되지 않아야 함
        verify(savePort, never()).save(any());
    }

}