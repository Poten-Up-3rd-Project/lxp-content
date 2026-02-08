package com.lxp.content.course.qna.infrastructure.persistence.repository;

import com.lxp.content.course.infra.persistence.mysql.write.entity.SectionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SectionReadRepository extends JpaRepository<SectionJpaEntity, Long> {
    Optional<SectionJpaEntity> findByUuid(String uuid);
}