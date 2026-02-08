package com.lxp.content.course.qna.infrastructure.persistence.entity;

import com.lxp.common.infrastructure.persistence.BaseJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "qna_answer", indexes = {
    @Index(name = "idx_qna_answer_qna_bid", columnList = "qnaBusinessId"),
    @Index(name = "uk_qna_answer_event_id", columnList = "eventId", unique = true)
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class QnaAnswerJpaEntity extends BaseJpaEntity {

    @Column(nullable = false)
    private String qnaBusinessId; // Qna.id (UUID)

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answerText;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private LocalDateTime answeredAt;

    @Column(nullable = false)
    private String source; // e.g., lxp-qna-engine

    @Column(nullable = false, unique = true)
    private String eventId; // for idempotency

    public QnaAnswerJpaEntity(
        String qnaBusinessId,
        String answerText,
        String model,
        LocalDateTime answeredAt,
        String source,
        String eventId
    ) {
        this.qnaBusinessId = qnaBusinessId;
        this.answerText = answerText;
        this.model = model;
        this.answeredAt = answeredAt;
        this.source = source;
        this.eventId = eventId;
    }
}
