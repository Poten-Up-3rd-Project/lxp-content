package com.lxp.content.course.infra.web.external.dto.response;

import java.util.List;

public record SectionResponse(
        String id,
        String title,
        long durationInSeconds,
        int order,
        List<LectureResponse> lectures
) {
}
