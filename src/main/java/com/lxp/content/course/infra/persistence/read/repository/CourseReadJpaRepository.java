package com.lxp.content.course.infra.persistence.read.repository;

import com.lxp.content.course.infra.persistence.read.entity.CourseReadJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseReadJpaRepository extends JpaRepository<CourseReadJpaEntity, String> {

    @Query("SELECT c FROM CourseReadJpaEntity c WHERE " +
            "LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.tagSearchText) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<CourseReadJpaEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
