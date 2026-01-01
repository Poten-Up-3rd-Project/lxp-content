package com.lxp.content.course.domain.service.spec;

public record InstructorSpec(
        String userId,
        String name,
        String role,
        String status
) {
}
