package com.lxp.content.course.qna.application.service;

import com.lxp.content.course.qna.application.port.in.AddQnaAnswerUseCase;
import com.lxp.content.course.qna.infrastructure.persistence.entity.QnaAnswerJpaEntity;
import com.lxp.content.course.qna.infrastructure.persistence.repository.QnaAnswerJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class QnaAnswerCommandServiceTest {

    QnaAnswerJpaRepository repository;
    QnaAnswerCommandService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(QnaAnswerJpaRepository.class);
        service = new QnaAnswerCommandService(repository);
    }

    @Test
    @DisplayName("idempotency: 동일 eventId가 있으면 새로 저장하지 않고 기존 id를 반환한다")
    void idempotentByEventId() {
        // given
        var existing = new QnaAnswerJpaEntity(
            "qna-1", "answer", "gpt-4o-mini", LocalDateTime.now(), "lxp-qna-engine", "evt-1");
        // 가짜 ID 설정
        try {
            var f = existing.getClass().getSuperclass().getDeclaredField("id");
            f.setAccessible(true);
            f.set(existing, 100L);
        } catch (Exception ignored) {
        }

        when(repository.findByEventId("evt-1")).thenReturn(Optional.of(existing));

        var cmd = new AddQnaAnswerUseCase.Command(
            "qna-1", "새답변", "gpt-4o-mini", LocalDateTime.now(), "lxp-qna-engine", "evt-1");

        // when
        var res = service.handle(cmd);

        // then
        assertThat(res.id()).isEqualTo(100L);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("eventId가 처음이면 저장 후 생성된 id를 반환한다")
    void saveNewAnswer() {
        // given
        when(repository.findByEventId("evt-2")).thenReturn(Optional.empty());

        ArgumentCaptor<QnaAnswerJpaEntity> captor = ArgumentCaptor.forClass(QnaAnswerJpaEntity.class);
        when(repository.save(captor.capture())).thenAnswer(invocation -> {
            QnaAnswerJpaEntity e = invocation.getArgument(0);
            // 가짜 ID 설정
            try {
                var f = e.getClass().getSuperclass().getDeclaredField("id");
                f.setAccessible(true);
                f.set(e, 200L);
            } catch (Exception ignored) {
            }
            return e;
        });

        var now = LocalDateTime.now();
        var cmd = new AddQnaAnswerUseCase.Command(
            "qna-2", "답변본문", "gpt-4o-mini", now, "lxp-qna-engine", "evt-2");

        // when
        var res = service.handle(cmd);

        // then
        assertThat(res.id()).isEqualTo(200L);
        var saved = captor.getValue();
        assertThat(saved.getQnaBusinessId()).isEqualTo("qna-2");
        assertThat(saved.getAnswerText()).isEqualTo("답변본문");
        assertThat(saved.getModel()).isEqualTo("gpt-4o-mini");
        assertThat(saved.getAnsweredAt()).isEqualTo(now);
        assertThat(saved.getSource()).isEqualTo("lxp-qna-engine");
        assertThat(saved.getEventId()).isEqualTo("evt-2");
    }
}
