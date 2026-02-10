package com.lxp.content.course.qna.infrastructure.persistence.repository;

import com.lxp.content.course.qna.infrastructure.persistence.entity.QnaAnswerJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QnaAnswerJpaRepository extends JpaRepository<QnaAnswerJpaEntity, Long> {

    Optional<QnaAnswerJpaEntity> findByEventId(String eventId);

    List<QnaAnswerJpaEntity> findByQnaBusinessIdOrderByAnsweredAtAsc(String qnaBusinessId);
}
