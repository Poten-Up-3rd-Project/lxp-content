package com.lxp.content.course.domain.rule;

import com.lxp.common.domain.exception.ErrorCode;
import com.lxp.common.domain.policy.BusinessRule;
import com.lxp.content.course.domain.exception.CourseErrorCode;
import com.lxp.content.course.domain.model.id.InstructorUUID;

public record CourseOwnerRule(InstructorUUID ownerId, InstructorUUID requesterId) implements BusinessRule {
    @Override
    public boolean isBroken() {
        return !ownerId.equals(requesterId);
    }

    @Override
    public String getMessage() {
        return "You do not have permission to modify this course.";
    }

    @Override
    public ErrorCode getErrorCode() {
        return CourseErrorCode.FORBIDDEN_COURSE_MODIFY;
    }
}