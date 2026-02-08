package com.lxp.content.course.qna.application;

import com.lxp.content.course.qna.application.port.in.CreateQnaUseCase;
import com.lxp.content.course.qna.application.port.out.SaveQnaPort;
import com.lxp.content.course.qna.application.service.QnaCommandService;
import com.lxp.content.course.qna.domain.model.Qna;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class QnaCommandServiceTest {

    @Mock
    SaveQnaPort saveQnaPort;
    @Mock
    ApplicationEventPublisher eventPublisher;

    QnaCommandService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new QnaCommandService(saveQnaPort, eventPublisher);
    }

    @Test
    void handle_persists_and_publishes_event() {
        // given
        CreateQnaUseCase.Command cmd = new CreateQnaUseCase.Command(
            "c", "s", "l", "a", "t", "body"
        );
        when(saveQnaPort.save(any())).thenAnswer(inv -> inv.getArgument(0, Qna.class));

        // when
        CreateQnaUseCase.Result res = service.handle(cmd);

        // then
        assertThat(res.id()).isNotBlank();
        verify(saveQnaPort, times(1)).save(any());
        verify(eventPublisher, times(1)).publishEvent(any(Object.class));
    }
}
