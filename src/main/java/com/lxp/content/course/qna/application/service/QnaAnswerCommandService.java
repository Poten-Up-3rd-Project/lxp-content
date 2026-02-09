package com.lxp.content.course.qna.application.service;

import com.lxp.content.course.qna.application.port.in.AddQnaAnswerUseCase;
import com.lxp.content.course.qna.infrastructure.persistence.entity.QnaAnswerJpaEntity;
import com.lxp.content.course.qna.infrastructure.persistence.repository.QnaAnswerJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QnaAnswerCommandService implements AddQnaAnswerUseCase {

    private final QnaAnswerJpaRepository repository;

    @Override
    @Transactional
    public Result handle(Command c) {
        // Diagnostics: incoming length
        int incomingLen = c.answerText() != null ? c.answerText().length() : 0;
        log.info("qna.answer.save.incoming qnaId={}, eventId={}, len={}", c.qnaId(), c.eventId(), incomingLen);

        // Idempotency by eventId
        var existing = repository.findByEventId(c.eventId());
        if (existing.isPresent()) {
            log.info("qna.answer.save.idempotent qnaId={}, eventId={}, id={}", c.qnaId(), c.eventId(), existing.get().getId());
            return new Result(existing.get().getId());
        }
        var entity = new QnaAnswerJpaEntity(
            c.qnaId(),
            c.answerText(),
            c.model(),
            c.answeredAt(),
            c.source(),
            c.eventId()
        );
        var saved = repository.save(entity);
        log.info("qna.answer.saved qnaId={}, eventId={}, id={}, len={}",
            saved.getQnaBusinessId(), saved.getEventId(), saved.getId(),
            saved.getAnswerText() != null ? saved.getAnswerText().length() : 0);
        return new Result(saved.getId());
    }
}
