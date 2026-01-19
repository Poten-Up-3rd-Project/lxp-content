package com.lxp.content.like.domain.model;

import com.lxp.common.domain.event.AggregateRoot;
import com.lxp.content.like.domain.exception.LikeErrorCode;
import com.lxp.content.like.domain.exception.LikeException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uq_userId_courseId",
                columnNames = {"user_id", "course_id"}
        ),
        indexes = @Index(name = "idx_course_id", columnList = "course_id")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like extends AggregateRoot<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private UUID userId;

    @Column(nullable = false, updatable = false)
    private UUID courseId;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Like(UUID userId, UUID courseId) {

        validate(userId, courseId);

        this.userId = userId;
        this.courseId = courseId;
    }

    private void validate(UUID userId, UUID courseId) {
        if (userId == null) {
            throw new LikeException(LikeErrorCode.USER_ID_IS_NULL);
        }

        if (courseId == null) {
            throw new LikeException(LikeErrorCode.COURSE_ID_IS_NULL);
        }
    }

    // ---------- getters

    public Long id() {
        return id;
    }

    public UUID userId() {
        return userId;
    }

    public UUID courseId() {
        return courseId;
    }

    @Override
    public Long getId() {
        return id;
    }
}
