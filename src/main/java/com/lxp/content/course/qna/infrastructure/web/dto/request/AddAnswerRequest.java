package com.lxp.content.course.qna.infrastructure.web.dto.request;

import java.time.LocalDateTime;

public record AddAnswerRequest(
    String answerText,
    String model,
    LocalDateTime answeredAt,
    String source,
    String eventId
) {
}
