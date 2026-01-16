package com.lxp.content.course.infra.persistence.mysql.read.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

// 나중에 분리
@Table(name = "course_read")
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseReadJpaEntity {
    @Id
    private String uuid;
    private String instructorId;
    private String instructorName;
    private String title;
    private String thumbnail;
    private String description;
    private String difficulty;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    private List<Tag> tags;
    private String tagSearchText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
