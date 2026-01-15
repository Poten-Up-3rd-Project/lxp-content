package com.lxp.content.course.infra.persistence.mysql.read.repository;

import com.lxp.content.course.infra.persistence.mysql.read.entity.CourseReadJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseReadJpaRepository extends JpaRepository<CourseReadJpaEntity, String> {

    @Query("SELECT c FROM CourseReadJpaEntity c WHERE " +
            "LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.tagSearchText) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<CourseReadJpaEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM CourseReadJpaEntity c WHERE " +
            "(:ids IS NULL OR c.uuid IN :ids) AND " +
            "(:difficulties IS NULL OR c.difficulty IN :difficulties) " +
            "ORDER BY c.createdAt DESC")
    List<CourseReadJpaEntity> filterCourses(
            @Param("ids") List<String> ids,
            @Param("difficulties") List<String> difficulties,
            Pageable pageable // limit 처리를 위해 Pageable 사용
    );
}
