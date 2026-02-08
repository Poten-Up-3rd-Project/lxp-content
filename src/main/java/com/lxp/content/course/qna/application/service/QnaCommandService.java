package com.lxp.content.course.qna.application.service;

import com.lxp.content.course.qna.application.port.in.CreateQnaUseCase;
import com.lxp.content.course.qna.application.port.out.SaveQnaPort;
import com.lxp.content.course.qna.domain.model.Qna;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QnaCommandService implements CreateQnaUseCase {

    private final SaveQnaPort saveQnaPort;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Result handle(Command command) {
        Qna.Result created = Qna.create(
            command.courseUuid(),
            command.sectionUuid(),
            command.lectureUuid(),
            command.authorId(),
            command.title(),
            command.content()
        );

        Qna saved = saveQnaPort.save(created.qna());
        eventPublisher.publishEvent(created.domainEvent());
        return new Result(saved.getId());
    }
}
