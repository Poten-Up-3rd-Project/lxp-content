package com.lxp.content.course.qna.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxp.content.course.qna.application.port.in.AddQnaAnswerUseCase;
import com.lxp.content.course.qna.application.port.in.CreateQnaUseCase;
import com.lxp.content.course.qna.application.port.in.GetQnaAnswersQuery;
import com.lxp.content.course.qna.application.port.in.GetQnaQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = QnaController.class)
@AutoConfigureMockMvc(addFilters = false)
class QnaControllerCallbackTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private CreateQnaUseCase createQnaUseCase;

    @MockitoBean
    private GetQnaAnswersQuery getQnaAnswersQuery;

    @MockitoBean
    GetQnaQuery getQnaQuery;

    @Autowired
    AddQnaAnswerUseCase addQnaAnswerUseCase;

    @Test
    @DisplayName("POST /api-v1/qna/{id}/answers → 201 Created")
    void postAnswerCreated() throws Exception {
        Mockito.when(addQnaAnswerUseCase.handle(any())).thenReturn(new AddQnaAnswerUseCase.Result(123L));

        var body = new java.util.HashMap<String, Object>();
        body.put("answerText", "테스트 답변");
        body.put("model", "gpt-4o-mini");
        body.put("answeredAt", LocalDateTime.now().toString());
        body.put("source", "lxp-qna-engine");
        body.put("eventId", "evt-xyz");

        mockMvc.perform(post("/api-v1/qna/{id}/answers", "qna-1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Idempotency-Key", "evt-xyz")
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(123L));
    }

    @TestConfiguration
    static class Mocks {
        @Bean
        AddQnaAnswerUseCase addQnaAnswerUseCase() {
            return Mockito.mock(AddQnaAnswerUseCase.class);
        }
    }
}
