package com.lxp.content.course.qna.application.port.in;

import java.time.LocalDateTime;
import java.util.List;

public interface GetQnaAnswersQuery {

    List<AnswerView> byQnaId(String qnaId);

    record AnswerView(
        Long id,
        String qnaId,
        String answerText,
        String model,
        LocalDateTime answeredAt,
        String source,
        String eventId
    ) {}
}