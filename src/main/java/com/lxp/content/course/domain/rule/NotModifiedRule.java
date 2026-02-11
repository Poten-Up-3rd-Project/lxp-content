package com.lxp.content.course.domain.rule;

import com.lxp.common.domain.exception.ErrorCode;
import com.lxp.common.domain.policy.BusinessRule;
import com.lxp.content.course.domain.exception.CourseErrorCode;
import com.lxp.content.course.domain.model.id.CourseUUID;

import java.time.Instant;
public record NotModifiedRule(Instant deleted, CourseUUID uuid) implements BusinessRule {

    @Override
    public boolean isBroken() {
        return deleted != null;
    }

    @Override
    public String getMessage() {
        return String.format("The course (UUID: %s) is already deleted and cannot be modified.", uuid);
    }

    @Override
    public ErrorCode getErrorCode() {
        return CourseErrorCode.COURSE_ALREADY_DELETED;
    }
}