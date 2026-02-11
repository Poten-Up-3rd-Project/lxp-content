package com.lxp.content.course.qna.infrastructure.persistence.repository;

import com.lxp.content.course.infra.persistence.mysql.write.entity.LectureJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LectureReadRepository extends JpaRepository<LectureJpaEntity, Long> {

    Optional<LectureJpaEntity> findByUuid(String uuid);
}
