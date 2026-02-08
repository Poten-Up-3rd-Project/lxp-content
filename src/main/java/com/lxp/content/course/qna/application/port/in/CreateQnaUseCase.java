package com.lxp.content.course.qna.application.port.in;

public interface CreateQnaUseCase {

    Result handle(Command command);

    record Command(
        String courseUuid,
        String sectionUuid,
        String lectureUuid,
        String authorId,
        String title,
        String content
    ) {
    }

    record Result(String id) {
    }
}
