package com.lxp.content.course.common.exception;

import com.lxp.common.domain.exception.DomainException;
import com.lxp.content.course.domain.exception.CourseErrorCode;

public class CourseNotFoundException extends DomainException {
    public CourseNotFoundException(String courseId) {
        super(
                CourseErrorCode.COURSE_NOT_FOUND,
                String.format("Course not found: %s", courseId)
        );
    }
}