package com.lxp.content.course.qna.infrastructure.persistence.repository;

import com.lxp.content.course.qna.infrastructure.persistence.entity.QnaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QnaJpaRepository extends JpaRepository<QnaJpaEntity, Long> {

    Optional<QnaJpaEntity> findByBusinessId(String businessId);

    List<QnaJpaEntity> findByLectureUuidOrderByCreatedAtDesc(String lectureUuid);
}
