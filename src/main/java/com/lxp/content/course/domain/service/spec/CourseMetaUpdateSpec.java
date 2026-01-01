package com.lxp.content.course.domain.service.spec;

import com.lxp.content.course.domain.model.enums.Level;
import com.lxp.content.course.domain.model.vo.CourseDescription;
import com.lxp.content.course.domain.model.vo.CourseTitle;

import java.util.Optional;

public record CourseMetaUpdateSpec(
        Optional<CourseTitle> title,
        Optional<CourseDescription> description,
        Optional<String> thumbnailUrl,
        Optional<Level> difficulty
) {
    public static CourseMetaUpdateSpec of(
            String title,
            String description,
            String thumbnailUrl,
            Level difficulty
    ) {
        return new CourseMetaUpdateSpec(
                Optional.ofNullable(title).map(CourseTitle::of),
                Optional.ofNullable(description).map(CourseDescription::of),
                Optional.ofNullable(thumbnailUrl),
                Optional.ofNullable(difficulty)
        );
    }

    public boolean hasChanges() {
        return title.isPresent()
                || description.isPresent()
                || thumbnailUrl.isPresent()
                || difficulty.isPresent();
    }
}