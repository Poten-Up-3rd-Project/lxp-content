package com.lxp.content.progress.infrastructure.web.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxp.content.progress.application.mapper.ProgressWebMapper;
import com.lxp.content.progress.application.port.in.usecase.UpdateProgressUseCase;
import com.lxp.content.progress.infrastructure.web.external.dto.UpdateProgressRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProgressExternalController.class)
@Disabled("시큐리티 반영 후 테스트 수정 필요")
@DisplayName("ProgressExternalController 테스트")
class ProgressExternalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private UpdateProgressUseCase updateProgressUseCase;

    @MockitoBean
    private ProgressWebMapper progressWebMapper;

    private String userId;
    private String courseId;
    private String lectureId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        courseId = UUID.randomUUID().toString();
        lectureId = UUID.randomUUID().toString();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("PATCH /api-v1/progress/{courseId}/{lectureId} 호출 시 성공 응답을 반환한다")
    void shouldReturnSuccessResponse_WhenCallPatchEndPoint() throws Exception {
        // given
        UpdateProgressRequest request = new UpdateProgressRequest(100);

        // Mapper가 Command를 반환하도록 설정 (내용은 중요하지 않으므로 any() 혹은 dummy)
        given(progressWebMapper.toCommand(eq(userId), eq(courseId), eq(lectureId), any()))
                .willReturn(null);

        // when & then
        mockMvc.perform(patch("/api-v1/progress/{courseId}/{lectureId}", courseId, lectureId)
                        .header("X-Passport", userId) //TODO 인증 정보에서 사용자 ID 가져오기
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.timestamp").exists());

        // UseCase가 실제로 호출되었는지 검증
        verify(updateProgressUseCase).execute(any());
    }

}