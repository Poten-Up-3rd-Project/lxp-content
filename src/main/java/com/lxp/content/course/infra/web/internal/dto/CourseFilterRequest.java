package com.lxp.content.course.infra.web.internal.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.List;

public record CourseFilterRequest(
        List<String> ids,
        List<String> difficulties,
        @Min(1) @Max(100) Integer limit
) {
    public CourseFilterRequest {
        if (limit == null) limit = 10;
    }
}
