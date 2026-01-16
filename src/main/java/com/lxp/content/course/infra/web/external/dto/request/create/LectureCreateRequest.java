package com.lxp.content.course.infra.web.external.dto.request.create;

public record LectureCreateRequest(
    String title,
    String videoUrl
) {
}
