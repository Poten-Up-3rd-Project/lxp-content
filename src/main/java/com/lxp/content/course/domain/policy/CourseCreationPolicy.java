package com.lxp.content.course.domain.policy;

import com.lxp.content.course.domain.exception.CourseException;
import com.lxp.content.course.domain.service.spec.CourseCreateSpec;
import com.lxp.content.course.domain.service.spec.InstructorSpec;
import com.lxp.content.course.domain.spec.ActiveInstructorSpecification;

public class CourseCreationPolicy {

    public void validate(CourseCreateSpec spec,InstructorSpec instructor) {
        if (!new ActiveInstructorSpecification().isSatisfiedBy(instructor)) {
            throw CourseException.InvalidInstructorException();
        }
    }
}
