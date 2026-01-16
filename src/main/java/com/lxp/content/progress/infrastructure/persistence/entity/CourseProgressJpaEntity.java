package com.lxp.content.progress.infrastructure.persistence.entity;

import com.lxp.content.progress.domain.model.enums.CourseProgressStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 강좌 진행도 JPA 엔티티
 */
@Entity
@Getter
@Table(name = "course_progresses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseProgressJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_progress_id")
    private Long id;

    @Column(nullable = false)
    private String businessId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String courseId;

    private float totalProgress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseProgressStatus progressStatus;

    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "courseProgress", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LectureProgressJpaEntity> lectureProgresses;

    @Builder
    private CourseProgressJpaEntity(Long id, String businessId, String userId, String courseId, float totalProgress, CourseProgressStatus progressStatus, LocalDateTime completedAt) {
        this.id = id;
        this.businessId = businessId;
        this.userId = userId;
        this.courseId = courseId;
        this.totalProgress = totalProgress;
        this.progressStatus = progressStatus;
        this.completedAt = completedAt;
    }

    public void updateFromDomain(float totalProgress, CourseProgressStatus progressStatus, LocalDateTime completedAt) {
        this.totalProgress = totalProgress;
        this.progressStatus = progressStatus;
        this.completedAt = completedAt;
    }

    public void addLectureProgress(LectureProgressJpaEntity lectureProgress) {
        if(lectureProgresses == null) {
            lectureProgresses = new ArrayList<>();
        }

        lectureProgresses.add(lectureProgress);
        lectureProgress.belongsTo(this);
    }
}
