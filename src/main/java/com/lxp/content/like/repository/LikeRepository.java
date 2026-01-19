package com.lxp.content.like.repository;

import com.lxp.content.like.domain.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    @Modifying(clearAutomatically = true)
    @Query(
            nativeQuery = true,
            value = "INSERT INTO like (user_id, course_id, created_at)" +
                    "VALUES (:userId, :courseId, :now)" +
                    "ON DUPLICATE KEY UPDATE id = id"
    )
    void saveIdempotently(
            @Param("userId") UUID userId,
            @Param("courseId") UUID courseId,
            @Param("now") Instant now
    );

    void deleteByUserIdAndCourseId(UUID userId, UUID courseId);

    List<Like> findAllByUserId(UUID userId);
}
