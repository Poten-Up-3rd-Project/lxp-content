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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = QnaController.class)
class QnaControllerAnswersQueryTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private CreateQnaUseCase createQnaUseCase;

    @MockitoBean
    private GetQnaQuery getQnaQuery;

    @Autowired
    private GetQnaAnswersQuery getQnaAnswersQuery;

    @Test
    @DisplayName("GET /api-v1/qna/{id}/answers → 200 OK and list payload")
    void getAnswersOk() throws Exception {
        var now = LocalDateTime.now();
        var list = List.of(
            new GetQnaAnswersQuery.AnswerView(1L, "qna-1", "A1", "gemini-3-flash-preview", now.minusMinutes(1), "lxp-qna-engine", "evt-1"),
            new GetQnaAnswersQuery.AnswerView(2L, "qna-1", "A2", "gemini-3-flash-preview", now, "lxp-qna-engine", "evt-2")
        );
        Mockito.when(getQnaAnswersQuery.byQnaId(anyString())).thenReturn(list);

        mockMvc.perform(get("/api-v1/qna/public/{id}/answers", "qna-1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].qnaId").value("qna-1"))
            .andExpect(jsonPath("$[0].answerText").value("A1"))
            .andExpect(jsonPath("$[1].id").value(2L));
    }

    @org.springframework.boot.test.context.TestConfiguration
    static class Mocks {
        @Bean
        GetQnaAnswersQuery getQnaAnswersQuery() {
            return Mockito.mock(GetQnaAnswersQuery.class);
        }

        @Bean
        AddQnaAnswerUseCase addQnaAnswerUseCase() {
            return Mockito.mock(AddQnaAnswerUseCase.class);
        }
    }
}
