package com.lxp.content.course.common.exception;

import com.lxp.common.domain.exception.DomainException;
import com.lxp.content.course.domain.exception.CourseErrorCode;

public class InstructorNotFoundException extends DomainException {

    public InstructorNotFoundException(String userId) {
        super(
                CourseErrorCode.INVALID_INSTRUCTOR,
                String.format("Instructor not found: %s", userId)
        );
    }
}
