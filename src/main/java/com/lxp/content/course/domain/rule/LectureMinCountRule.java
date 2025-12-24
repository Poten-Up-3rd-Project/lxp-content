package com.lxp.content.course.domain.rule;

import com.lxp.common.domain.exception.ErrorCode;
import com.lxp.common.domain.policy.BusinessRule;
import com.lxp.content.course.domain.exception.CourseErrorCode;
import com.lxp.content.course.domain.model.collection.CourseSections;

public record LectureMinCountRule(CourseSections sections) implements BusinessRule {

    private static final int MIN_LECTURE_COUNT = 1;

    @Override
    public boolean isBroken() {
        return sections.values().stream()
                .mapToLong(section -> section.lectures().values().size())
                .sum() < MIN_LECTURE_COUNT;
    }

    @Override
    public String getMessage() {
        return String.format("Course must have at least %d lecture(s)",MIN_LECTURE_COUNT );
    }

    @Override
    public ErrorCode getErrorCode() {
        return CourseErrorCode.LECTURE_MIN_COUNT_VIOLATION;
    }
}
