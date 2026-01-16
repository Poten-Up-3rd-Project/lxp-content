package com.lxp.content.course.infra.persistence.mysql.write.entity;

import com.lxp.common.infrastructure.persistence.BaseJpaEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Table(name = "section")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SectionJpaEntity extends BaseJpaEntity {

    @Getter
    @Column(nullable = false, unique = true)
    private String uuid;

    @Getter
    @Column(nullable = false)
    private String title;

    @Getter
    @Column(name = "sort_order")
    private int order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private CourseJpaEntity course;

    @Getter
    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("order ASC")
    private List<LectureJpaEntity> lectures = new ArrayList<>();

    @Builder
    public SectionJpaEntity(String uuid, String title, int order) {
        this.uuid = uuid;
        this.title = title;
        this.order = order;
    }

    public void assignCourse(CourseJpaEntity course) {
        this.course = course;
    }

    public void addLecture(LectureJpaEntity lecture) {
        this.lectures.add(lecture);
        lecture.assignSection(this);
    }
}
