package com.lxp.content.course.infra.persistence.mysql.read.mapper;

import com.lxp.common.application.port.out.DomainMapper;
import com.lxp.content.course.application.projection.CourseReadModel;
import com.lxp.content.course.domain.model.enums.Level;
import com.lxp.content.course.infra.persistence.mysql.read.entity.CourseReadJpaEntity;
import com.lxp.content.course.infra.persistence.mysql.read.entity.Tag;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CourseReadEntityMapper implements DomainMapper<CourseReadModel, CourseReadJpaEntity> {
    @Override
    public CourseReadModel toDomain(CourseReadJpaEntity entity) {
        return new CourseReadModel(
                entity.getUuid(),
                entity.getInstructorId(),
                entity.getInstructorName(),
                entity.getThumbnail(),
                entity.getTitle(),
                entity.getDescription(),
                Level.valueOf(entity.getDifficulty()),
                entity.getTags().stream()
                        .map(t -> new CourseReadModel.TagReadModel(t.id(), t.content(), t.color(), t.variant()))
                        .toList(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    @Override
    public CourseReadJpaEntity toEntity(CourseReadModel domain) {
        String tagSearchText = domain.tags().stream()
                .map(CourseReadModel.TagReadModel::content)
                .collect(Collectors.joining(","));

        return CourseReadJpaEntity.builder()
                .uuid(domain.uuid())
                .instructorId(domain.instructorId())
                .instructorName(domain.instructorName())
                .thumbnail(domain.thumbnailUrl())
                .title(domain.title())
                .description(domain.description())
                .difficulty(domain.difficulty().name())
                .tags(domain.tags().stream()
                        .map(t -> new Tag(t.id(), t.content(), t.color(), t.variant()))
                        .toList())
                .tagSearchText(tagSearchText)
                .createdAt(domain.createdAt())
                .updatedAt(domain.updatedAt())
                .build();
    }
}
