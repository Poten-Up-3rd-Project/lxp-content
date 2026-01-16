package com.lxp.content.progress.infrastructure.persistence.entity;

import com.lxp.content.progress.domain.model.enums.LectureProgressStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 강의 진행도 JPA 엔티티
 */
@Entity
@Table(name = "lecture_progresses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureProgressJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_progress_id")
    private Long id;

    @Column(nullable = false)
    private String businessId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String lectureId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LectureProgressStatus progressStatus;

    private Integer lastPlayedTime;

    @Column(nullable = false)
    private Integer totalDuration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_progress_id")
    private CourseProgressJpaEntity courseProgress;

    @Builder
    public LectureProgressJpaEntity(Long id, String businessId, String userId, String lectureId, LectureProgressStatus progressStatus, Integer lastPlayedTime, Integer totalDuration) {
        this.id = id;
        this.businessId = businessId;
        this.userId = userId;
        this.lectureId = lectureId;
        this.progressStatus = progressStatus;
        this.lastPlayedTime = lastPlayedTime;
        this.totalDuration = totalDuration;
    }

    public void update(Integer lastPlayedTime, LectureProgressStatus progressStatus) {
        this.lastPlayedTime = lastPlayedTime;
        this.progressStatus = progressStatus;
    }

    public void belongsTo(CourseProgressJpaEntity courseProgress) {
        this.courseProgress = courseProgress;
    }

}
