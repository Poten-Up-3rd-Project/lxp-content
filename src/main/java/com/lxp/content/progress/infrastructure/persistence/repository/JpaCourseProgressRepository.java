package com.lxp.content.progress.infrastructure.persistence.repository;

import com.lxp.content.progress.infrastructure.persistence.entity.CourseProgressJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 강좌 진행률 JPA 리포지토리
 */
@Repository
public interface JpaCourseProgressRepository extends JpaRepository<CourseProgressJpaEntity, Long> {

    @Query("SELECT DISTINCT cp FROM CourseProgressJpaEntity cp " +
            "LEFT JOIN FETCH cp.lectureProgresses " +
            "WHERE cp.userId = :userId")
    List<CourseProgressJpaEntity> findByUserIdWithLecture(String userId);

    @Query("SELECT cp FROM CourseProgressJpaEntity cp " +
            "LEFT JOIN FETCH cp.lectureProgresses " +
            "WHERE cp.courseId = :courseId")
    Optional<CourseProgressJpaEntity> findByCourseIdWithLecture(String courseId);

    @Query("SELECT cp FROM CourseProgressJpaEntity cp " +
            "LEFT JOIN FETCH cp.lectureProgresses " +
            "WHERE cp.businessId = :businessId")
    Optional<CourseProgressJpaEntity> findByBusinessIdWithLecture(String businessId);

    @Query("SELECT cp FROM CourseProgressJpaEntity cp " +
            "LEFT JOIN FETCH cp.lectureProgresses " +
            "WHERE cp.userId = :userId " +
            "AND cp.courseId = :courseId")
    Optional<CourseProgressJpaEntity> findByUserIdAndCourseIdWithLectures(String userId, String courseId);
}