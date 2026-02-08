package com.lxp.content.course.qna.infrastructure.persistence.entity;

import com.lxp.common.infrastructure.persistence.BaseJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "qna")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class QnaJpaEntity extends BaseJpaEntity {

    @Column(nullable = false, unique = true)
    private String businessId; // Qna id (UUID string)

    @Column(nullable = false)
    private String courseUuid;

    @Column(nullable = false)
    private String sectionUuid;

    @Column(nullable = false)
    private String lectureUuid;

    @Column(nullable = false)
    private String authorId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public QnaJpaEntity(String businessId, String courseUuid, String sectionUuid, String lectureUuid, String authorId, String title, String content, LocalDateTime createdAt) {
        this.businessId = businessId;
        this.courseUuid = courseUuid;
        this.sectionUuid = sectionUuid;
        this.lectureUuid = lectureUuid;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }
}