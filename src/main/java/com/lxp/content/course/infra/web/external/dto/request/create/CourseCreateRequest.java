package com.lxp.content.course.infra.web.external.dto.request.create;

import com.lxp.content.course.domain.model.enums.Level;

import java.util.List;

//TODO("valid 체크 ")
public record CourseCreateRequest(
    String title,
    String description,
    String thumbnailUrl,
    Level level,
    List<Long> tags,
    List<SectionCreateRequest> sections
) {
}
