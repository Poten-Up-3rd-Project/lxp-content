package com.lxp.content.course.infra.web.external.dto.response;

public record LectureResponse(
        String id,
        String title,
        String videoUrl,
        int order,
        long durationInSeconds

) {
}
