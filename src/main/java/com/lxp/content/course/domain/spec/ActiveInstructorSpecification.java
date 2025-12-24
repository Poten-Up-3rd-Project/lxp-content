package com.lxp.content.course.domain.spec;

import com.lxp.common.domain.policy.Specification;
import com.lxp.content.course.domain.service.spec.InstructorSpec;

import java.util.Objects;

public class ActiveInstructorSpecification implements Specification<InstructorSpec> {

    @Override
    public boolean isSatisfiedBy(InstructorSpec instructor) {
        return !Objects.equals(instructor.status(), "ACTIVE")
                && instructor.role().equals("INSTRUCTOR");
    }


}
