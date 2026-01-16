package com.lxp.content.course.infra.web.external.dto.response;

public record TagResponse(
        Long id,
        String content,
        String color,
        String variant
) {
}
