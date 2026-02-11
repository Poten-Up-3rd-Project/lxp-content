package com.lxp.content.course.qna.application.port.in;

import java.time.LocalDateTime;
import java.util.List;

public interface GetQnaQuery {

    QnaView byId(String qnaId);

    List<QnaView> byLecture(String lectureUuid);

    record QnaView(
        String id,
        String courseUuid,
        String sectionUuid,
        String lectureUuid,
        String authorId,
        String title,
        String content,
        LocalDateTime createdAt
    ) {
    }
}
