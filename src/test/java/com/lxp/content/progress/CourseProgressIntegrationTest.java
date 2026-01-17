package com.lxp.content.progress;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxp.content.common.passport.PassportClaims;
import com.lxp.content.common.passport.PassportExtractor;
import com.lxp.content.common.passport.PassportVerifier;
import com.lxp.content.course.application.port.provider.usecase.query.CourseDetailUseCase;
import com.lxp.content.progress.application.mapper.ProgressWebMapper;
import com.lxp.content.progress.application.port.in.query.GetLectureProgressListQuery;
import com.lxp.content.progress.domain.model.enums.CourseProgressStatus;
import com.lxp.content.progress.domain.model.enums.LectureProgressStatus;
import com.lxp.content.progress.exception.ProgressDomainException;
import com.lxp.content.progress.exception.ProgressErrorCode;
import com.lxp.content.progress.infrastructure.persistence.entity.CourseProgressJpaEntity;
import com.lxp.content.progress.infrastructure.persistence.entity.LectureProgressJpaEntity;
import com.lxp.content.progress.infrastructure.persistence.repository.JpaCourseProgressRepository;
import com.lxp.content.progress.infrastructure.web.external.dto.UpdateProgressRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("강좌 진행률 통합 테스트")
public class CourseProgressIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JpaCourseProgressRepository jpaRepository;

    @Autowired
    private ProgressWebMapper progressWebMapper;

    @MockitoBean
    private CourseDetailUseCase courseDetailUseCase;

    @MockitoBean
    private PassportVerifier passportVerifier;

    private String userId;
    private String courseId;
    private String lectureId1, lectureId2;
    private String cpBusinessId;
    private String lpBusinessId1, lpBusinessId2;
    private Integer totalDuration;

    private CourseProgressJpaEntity cpEntity;
    private LectureProgressJpaEntity lpEntity1, lpEntity2;

    @BeforeEach
    void setUp() {
        PassportClaims mockPassport = new PassportClaims(
            "user-123",
            List.of("ROLE_USER"),
            UUID.randomUUID().toString()
        );

        given(passportVerifier.verify(anyString()))
                .willReturn(mockPassport);

        userId = UUID.randomUUID().toString();
        courseId = UUID.randomUUID().toString();
        lectureId1 = UUID.randomUUID().toString();
        lectureId2 = UUID.randomUUID().toString();
        cpBusinessId = UUID.randomUUID().toString();
        lpBusinessId1 = UUID.randomUUID().toString();
        lpBusinessId2 = UUID.randomUUID().toString();

        totalDuration = 700;

        cpEntity = CourseProgressJpaEntity.builder()
                .id(null)
                .businessId(cpBusinessId)
                .userId(userId)
                .courseId(courseId)
                .totalProgress(0)
                .progressStatus(CourseProgressStatus.IDLE)
                .completedAt(null)
                .build();

        lpEntity1 = LectureProgressJpaEntity.builder()
                .id(null)
                .businessId(lpBusinessId1)
                .userId(userId)
                .lectureId(lectureId1)
                .lastPlayedTime(0)
                .progressStatus(LectureProgressStatus.NOT_STARTED)
                .totalDuration(totalDuration)
                .build();

        lpEntity2 = LectureProgressJpaEntity.builder()
                .id(null)
                .businessId(lpBusinessId2)
                .userId(userId)
                .lectureId(lectureId2)
                .lastPlayedTime(0)
                .progressStatus(LectureProgressStatus.NOT_STARTED)
                .totalDuration(totalDuration)
                .build();

        cpEntity.addLectureProgress(lpEntity1);
        cpEntity.addLectureProgress(lpEntity2);
    }

    /* -------------------------------------------------------------------------- */
    /* 외부 API (조회) 테스트 케이스                                          */
    /* -------------------------------------------------------------------------- */
    @Test
    @DisplayName("TC-CPI-001: 사용자가 강의 시청 시간을 업데이트하면 DB에 최종 상태가 반영되어야 한다(강좌 부분 완료)")
    void shouldReflectDatabase_WhenUpdateLectureWatchingTimeByUser() throws Exception {
        // 어댑터를 통하지 않고 직접 JpaRepository로 초기 데이터를 세팅합니다.
        Integer lastPlayedTime = 700;
        jpaRepository.save(cpEntity);

        // When: API 호출 (진행률 업데이트 요청)
        UpdateProgressRequest request = new UpdateProgressRequest(lastPlayedTime);

        mockMvc.perform(patch("/api-v1/progress/{courseId}/{lectureId}", courseId, lectureId1)
                        .header("X-Passport", userId)   //TODO 인증 정보에서 사용자 ID 가져오기
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Then: DB에 실제 값이 변경되었는지 검증
        var updatedEntity = jpaRepository.findByBusinessIdWithLecture(cpBusinessId)
                .orElseThrow();
        // Then 섹션 시작 부분에 추가
        System.out.println("Total Progress: " + updatedEntity.getTotalProgress());
        System.out.println("Lecture Progress Size: " + updatedEntity.getLectureProgresses().size());

        assertEquals(cpBusinessId, updatedEntity.getBusinessId(), "businessId가 매핑되어야 한다");
        assertEquals(userId, updatedEntity.getUserId(), "userId가 매핑되어야 한다");
        assertEquals(courseId, updatedEntity.getCourseId(), "courseId가 매핑되어야 한다");
        assertNotEquals(0, updatedEntity.getTotalProgress(), "totalProgress가 업데이트되어야 한다");
        assertEquals(CourseProgressStatus.IN_PROGRESS, updatedEntity.getProgressStatus(), "progressStatus가 IN_PROGRESS로 업데이트되어야 한다");
        assertNull(updatedEntity.getCompletedAt(), "completedAt는 아직 null이어야 한다");

        assertEquals(lpBusinessId1, updatedEntity.getLectureProgresses().get(0).getBusinessId(), "LectureProgress의 businessId가 매핑되어야 한다");
        assertEquals(lastPlayedTime, updatedEntity.getLectureProgresses().get(0).getLastPlayedTime(), "LectureProgress의 lastPlayedTime이 업데이트 한 시간과 매핑되어야 한다");
        assertEquals(LectureProgressStatus.COMPLETED, updatedEntity.getLectureProgresses().get(0).getProgressStatus(), "LectureProgress의 progressStatus가 COMPLETED로 업데이트되어야 한다");
        assertEquals(totalDuration, updatedEntity.getLectureProgresses().get(0).getTotalDuration(), "LectureProgress의 totalDuration이 매핑되어야 한다");
        assertEquals(cpEntity, updatedEntity.getLectureProgresses().get(0).getCourseProgress(), "LectureProgress의 CourseProgress가 매핑되어야 한다");
    }

    @Test
    @DisplayName("TC-CPI-002: 사용자가 강의 시청 시간을 업데이트하면 DB에 최종 상태가 반영되어야 한다(강좌 완료)")
    void shouldReflectDatabaseAsCompleted_WhenUpdateLectureWatchingTimeByUser() throws Exception {
        // 어댑터를 통하지 않고 직접 JpaRepository로 초기 데이터를 세팅합니다.
        Integer lastPlayedTime = 700; // 500초 시청
        jpaRepository.save(cpEntity);

        // When: API 호출 (진행률 업데이트 요청)
        UpdateProgressRequest request = new UpdateProgressRequest(lastPlayedTime);

        // 첫번째 강의 미리 완료 처리
        cpEntity.getLectureProgresses().get(0).update(lastPlayedTime, LectureProgressStatus.COMPLETED);

        mockMvc.perform(patch("/api-v1/progress/{courseId}/{lectureId}", courseId, lectureId2)
                        .header("X-Passport", userId)   //TODO 인증 정보에서 사용자 ID 가져오기
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Then: DB에 실제 값이 변경되었는지 검증
        var updatedEntity = jpaRepository.findByBusinessIdWithLecture(cpBusinessId)
                .orElseThrow();
        // Then 섹션 시작 부분에 추가
        System.out.println("Total Progress: " + updatedEntity.getTotalProgress());
        System.out.println("Lecture Progress Size: " + updatedEntity.getLectureProgresses().size());

        assertEquals(cpBusinessId, updatedEntity.getBusinessId(), "businessId가 매핑되어야 한다");
        assertEquals(userId, updatedEntity.getUserId(), "userId가 매핑되어야 한다");
        assertEquals(courseId, updatedEntity.getCourseId(), "courseId가 매핑되어야 한다");
        assertNotEquals(0, updatedEntity.getTotalProgress(), "totalProgress가 업데이트되어야 한다");
        assertEquals(CourseProgressStatus.COMPLETED, updatedEntity.getProgressStatus(), "progressStatus가 IN_PROGRESS로 업데이트되어야 한다");
        assertNotNull(updatedEntity.getCompletedAt(), "completedAt는 완료 시각으로 업데이트 되어야 한다");

        assertEquals(lpBusinessId1, updatedEntity.getLectureProgresses().get(0).getBusinessId(), "LectureProgress의 businessId가 매핑되어야 한다");
        assertEquals(lastPlayedTime, updatedEntity.getLectureProgresses().get(0).getLastPlayedTime(), "LectureProgress의 lastPlayedTime이 업데이트 한 시간과 매핑되어야 한다");
        assertEquals(LectureProgressStatus.COMPLETED, updatedEntity.getLectureProgresses().get(0).getProgressStatus(), "LectureProgress의 progressStatus가 COMPLETED로 업데이트되어야 한다");
        assertEquals(totalDuration, updatedEntity.getLectureProgresses().get(0).getTotalDuration(), "LectureProgress의 totalDuration이 매핑되어야 한다");
        assertEquals(cpEntity, updatedEntity.getLectureProgresses().get(0).getCourseProgress(), "LectureProgress의 CourseProgress가 매핑되어야 한다");
    }

    /* -------------------------------------------------------------------------- */
    /* 내부 API (조회) 테스트 케이스 추가                                              */
    /* -------------------------------------------------------------------------- */

    @Test
    @DisplayName("TC-CPI-003: 특정 사용자의 전체 강좌 진행률 리스트를 조회한다")
    void shouldReturnActiveCourseProgressList() throws Exception {
        // Given: DB에 초기 데이터 저장
        jpaRepository.save(cpEntity);

        // When & Then
        mockMvc.perform(get("/api-v1/internal/progress/users/courses")
                        .header("X-Passport", userId)   //TODO 인증 정보에서 사용자 ID 가져오기
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].courseId").value(courseId))
                .andExpect(jsonPath("$[0].totalProgress").exists());
    }

    @Test
    @DisplayName("TC-CPI-004: 특정 강좌 내의 상세 강의 진행률 리스트를 조회한다")
    void shouldReturnLectureProgressListInCourse() throws Exception {
        // Given: DB에 초기 데이터 저장
        jpaRepository.save(cpEntity);

        // When & Then
        mockMvc.perform(get("/api-v1/internal/progress/users/courses/{courseId}", courseId)
                        .header("X-Passport", userId)   //TODO 인증 정보에서 사용자 ID 가져오기
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)) // setUp에서 강의 2개 추가함
                .andExpect(jsonPath("$[0].lectureId").exists())
                .andExpect(jsonPath("$[0].totalDuration").value(totalDuration));
    }

    @Test
    @DisplayName("TC-CPI-005: 존재하지 않는 강좌 ID로 강의 리스트 조회 시 400(또는 500) 에러를 반환한다")
    void shouldReturnErrorWhenCourseNotFound() throws Exception {
        // Given
        String invalidCourseId = "non-existent-id";

        given(courseDetailUseCase.execute(any()))
                .willThrow(new ProgressDomainException(ProgressErrorCode.COURSE_PROGRESS_NOT_FOUND));

        // When & Then
        // Service 로직 상 .orElseThrow()가 발생하므로, 전역 예외 처리기(GlobalExceptionHandler) 설정에 따라
        // 400 혹은 500 응답이 오는지 확인합니다.
        mockMvc.perform(get("/api-v1/internal/progress/users/courses/{courseId}", invalidCourseId)
            .header("X-Passport", userId)
        )
            .andDo(print()) // 응답 구조 확인용
            .andExpect(status().isNotFound()) // 404 확인
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("PROGRESS_001")) // Enum에 정의된 코드
            .andExpect(jsonPath("$.error.message").value(ProgressErrorCode.COURSE_PROGRESS_NOT_FOUND.getMessage()));
    }

}
