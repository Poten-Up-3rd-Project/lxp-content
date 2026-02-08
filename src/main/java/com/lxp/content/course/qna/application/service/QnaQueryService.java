package com.lxp.content.course.qna.application.service;

import com.lxp.content.course.qna.application.port.in.GetQnaQuery;
import com.lxp.content.course.qna.application.port.out.LoadQnaPort;
import com.lxp.content.course.qna.domain.model.Qna;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QnaQueryService implements GetQnaQuery {

    private final LoadQnaPort loadQnaPort;

    @Override
    public QnaView byId(String qnaId) {
        Qna qna = loadQnaPort.findById(qnaId).orElseThrow(() -> new IllegalArgumentException("QnA not found: " + qnaId));
        return toView(qna);
    }

    @Override
    public List<QnaView> byLecture(String lectureUuid) {
        return loadQnaPort.findByLectureUuid(lectureUuid).stream().map(this::toView).toList();
    }

    private QnaView toView(Qna q) {
        return new QnaView(
            q.getId(),
            q.getCourseUuid(),
            q.getSectionUuid(),
            q.getLectureUuid(),
            q.getAuthorId(), q
            .getTitle(),
            q.getContent(),
            q.getCreatedAt()
        );
    }
}
