package com.lxp.content.course.domain.service.spec;


import com.lxp.content.course.domain.model.enums.Level;
import java.util.List;

public record CourseCreateSpec(
        String instructorId,
        String thumbnailUrl,
        String title,
        String description,
        Level level,
        List<SectionCreateSpec> sections,
        List<Long> tags
) {
    public record SectionCreateSpec(
            String title,
            List<LectureCreateSpec> lectures
    ) {}

    public record LectureCreateSpec(
            String title,
            String videoUrl
    ) {}
}