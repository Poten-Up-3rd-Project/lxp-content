package com.lxp.content.course.infra.persistence.write.repository;

import com.lxp.content.course.infra.persistence.write.entity.CourseJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CourseJpaRepository extends JpaRepository<CourseJpaEntity, Long> {
    Optional<CourseJpaEntity> findByUuid(String uuid);
    List<CourseJpaEntity> findAllByUuidIn(Collection<String> uuids);
}
