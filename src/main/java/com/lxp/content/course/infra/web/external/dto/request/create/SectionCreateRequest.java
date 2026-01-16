package com.lxp.content.course.infra.web.external.dto.request.create;

import java.util.List;

public record SectionCreateRequest(
    String title,
    List<LectureCreateRequest> lectures
) {
}
