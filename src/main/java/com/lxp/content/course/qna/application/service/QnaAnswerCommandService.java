package com.lxp.content.course.qna.application.service;

import com.lxp.content.course.qna.application.port.in.AddQnaAnswerUseCase;
import com.lxp.content.course.qna.infrastructure.persistence.entity.QnaAnswerJpaEntity;
import com.lxp.content.course.qna.infrastructure.persistence.repository.QnaAnswerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QnaAnswerCommandService implements AddQnaAnswerUseCase {

    private final QnaAnswerJpaRepository repository;

    @Override
    @Transactional
    public Result handle(Command c) {
        // Idempotency by eventId
        var existing = repository.findByEventId(c.eventId());
        if (existing.isPresent()) {
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
        return new Result(saved.getId());
    }
}
