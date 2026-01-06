package com.lxp.content.course.application.port.provider.command;

import com.lxp.common.application.cqrs.Command;
import com.lxp.content.course.domain.model.enums.Level;

import java.util.List;

public record CourseCreateCommand(
        String instructorId,
        String title,
        String description,
        String thumbnailUrl,
        Level level,
        List<Long> tags,
        List<SectionCreateCommand> sections
) implements Command {
    public record SectionCreateCommand(
            String title,
            List<LectureCreateCommand> lectures
    ) {
        public record LectureCreateCommand(
                String title,
                String videoUrl
        ) {}
    }

}