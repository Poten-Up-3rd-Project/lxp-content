package com.lxp.content.course.application.port.provider.view;

import com.lxp.common.application.cqrs.Query;
import com.lxp.content.course.domain.model.enums.Level;

import java.time.Instant;
import java.util.List;

public record CourseDetailView(
        String courseId,
        String title,
        String description,
        InstructorView Instructor,
        String thumbnailUrl,
        Level level,
        List<SectionView> sections,
        List<TagInfoView> tags,
        Instant createdAt,
        Instant updatedAt,
        Long durationOnMinutes
) implements Query<CourseDetailView> {

    public record SectionView(
            String sectionId,
            String title,
            Long durationOnSecond,
            int order,
            List<LectureView> lectures
    ) {

        public record LectureView(
                String lectureId,
                String title,
                String videoUrl,
                int order,
                Long duration
        ) {
        }

    }
}

