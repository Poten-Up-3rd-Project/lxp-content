package com.lxp.content.course.domain.rule;

import com.lxp.common.domain.exception.ErrorCode;
import com.lxp.common.domain.policy.BusinessRule;
import com.lxp.content.course.domain.exception.CourseErrorCode;
import com.lxp.content.course.domain.model.collection.CourseTags;

public record TagMinCountRule(CourseTags tags) implements BusinessRule {
    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 5;

    @Override
    public boolean isBroken() {
        return tags.size() < MIN_SIZE || tags.size() > MAX_SIZE;
    }

    @Override
    public String getMessage() {
        return "Course must have between " + MIN_SIZE + " and " + MAX_SIZE + " tags.";
    }

    @Override
    public ErrorCode getErrorCode() {
        return CourseErrorCode.TAG_VIOLATION;
    }
}
