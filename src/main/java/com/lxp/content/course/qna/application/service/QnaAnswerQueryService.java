package com.lxp.content.course.qna.application.service;

import com.lxp.content.course.qna.application.port.in.GetQnaAnswersQuery;
import com.lxp.content.course.qna.infrastructure.persistence.repository.QnaAnswerJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QnaAnswerQueryService implements GetQnaAnswersQuery {

    private final QnaAnswerJpaRepository repository;

    @Override
    public List<AnswerView> byQnaId(String qnaId) {
        var list = repository.findByQnaBusinessIdOrderByAnsweredAtAsc(qnaId)
            .stream()
            .map(e -> new AnswerView(
                e.getId(),
                e.getQnaBusinessId(),
                e.getAnswerText(),
                e.getModel(),
                e.getAnsweredAt(),
                e.getSource(),
                e.getEventId()
            ))
            .toList();

        // Diagnose potential truncation outside the service (e.g., gateway/response wrapper)
        list.forEach(v -> {
            int len = v.answerText() != null ? v.answerText().length() : 0;
            String head = v.answerText() != null ? v.answerText().substring(0, Math.min(32, len)) : "";
            String tail = v.answerText() != null ? v.answerText().substring(Math.max(0, len - Math.min(32, len))) : "";
            log.info(
                "qna.answer.serving qnaId={}, id={}, len={}, head='{}', tail='{}'",
                v.qnaId(), v.id(), len, head, tail
            );
        });

        return list;
    }
}
