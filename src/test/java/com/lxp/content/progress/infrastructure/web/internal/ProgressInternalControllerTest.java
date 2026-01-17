package com.lxp.content.progress.infrastructure.web.internal;

import com.lxp.content.common.passport.PassportClaims;
import com.lxp.content.common.passport.PassportExtractor;
import com.lxp.content.common.passport.PassportVerifier;
import com.lxp.content.progress.application.mapper.ProgressWebMapper;
import com.lxp.content.progress.application.port.in.command.CreateProgressCommand;
import com.lxp.content.progress.application.port.in.usecase.CreateCourseProgressUseCase;
import com.lxp.content.progress.application.port.in.usecase.GetActiveCourseProgressUseCase;
import com.lxp.content.progress.application.port.in.usecase.GetLectureProgressListUseCase;
import com.lxp.content.progress.exception.ProgressDomainException;
import com.lxp.content.progress.exception.ProgressErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProgressInternalController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProgressInternalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetActiveCourseProgressUseCase getActiveCourseProgressUseCase;
    @MockitoBean
    private GetLectureProgressListUseCase getLectureProgressListUseCase;
    @MockitoBean
    private CreateCourseProgressUseCase createCourseProgressUseCase;
    @MockitoBean
    private ProgressWebMapper mapper;
    @MockitoBean
    private PassportVerifier passportVerifier;
    @MockitoBean
    private PassportExtractor passportExtractor;

    @BeforeEach
    void setUp() {
        PassportClaims mockPassport = new PassportClaims(
                "user-123",
                List.of("ROLE_USER"),
                UUID.randomUUID().toString()
        );

        given(passportVerifier.verify(anyString()))
                .willReturn(mockPassport);

        given(passportExtractor.extract(any()))
                .willReturn("some-token");
    }

    @Test
    @DisplayName("TC-PIC-001: POST /api-v1/internal/progress/{courseId} 호출 시 201 응답을 반환해야 한다")
    void createProgressTest() throws Exception {
        // given
        String courseId = "course-abc";
        String userId = "user-123";
        CreateProgressCommand mockCommand = new CreateProgressCommand(userId, courseId);

        // Mapper가 Controller 내에서 어떻게 동작할지 정의
        when(mapper.toCreateCourseProgressCommand(anyString(), eq(courseId)))
                .thenReturn(mockCommand);

        // when & then
        mockMvc.perform(post("/api-v1/internal/progress/{courseId}", courseId)
                        .header("X-Passport", userId)) // Passport 헤더 포함
                .andExpect(status().isCreated());

        verify(createCourseProgressUseCase).execute(mockCommand);
    }

    @Test
    @DisplayName("TC-PIC-002: 강좌 진행도 생성 중 도메인 예외 발생 시 InternalHandler가 규격화된 응답을 반환한다")
    void createProgressFailTest() throws Exception {
        // 1. Given: 테스트 데이터 및 상황 설정
        String courseId = "invalid-course";
        String userId = "user-1";

        // Mapper가 userId와 courseId를 받아서 Command를 생성하도록 모킹
        CreateProgressCommand mockCommand = new CreateProgressCommand(userId, courseId);
        when(mapper.toCreateCourseProgressCommand(eq(userId), eq(courseId)))
                .thenReturn(mockCommand);

        // UseCase가 COURSE_NOT_FOUND 에러를 가진 예외를 던지도록 설정
        doThrow(new ProgressDomainException(ProgressErrorCode.COURSE_NOT_FOUND))
                .when(createCourseProgressUseCase).execute(any(CreateProgressCommand.class));

        // 2. When & Then: 호출 및 검증
        mockMvc.perform(post("/api-v1/internal/progress/{courseId}", courseId)
                        .header("X-Passport", userId) // 컨트롤러 @RequestHeader 명칭과 일치
                )
                .andDo(print()) // 에러 발생 시 로그 확인을 위해 추가
                .andExpect(status().isNotFound()) // 404 확인
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("PROGRESS_004")) // Enum의 "PROGRESS_004"와 일치
                .andExpect(jsonPath("$.error.message").value("정보에 해당하는 강좌가 존재하지 않습니다"));
    }
}