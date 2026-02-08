package com.lxp.content.course.qna.application.port.in;

import java.time.LocalDateTime;

public interface AddQnaAnswerUseCase {

    Result handle(Command command);

    record Command(
        String qnaId,
        String answerText,
        String model,
        LocalDateTime answeredAt,
        String source,
        String eventId
    ) {
    }

    record Result(Long id) {
    }
}
