package com.lxp.content.course.infra.web.client.response;

import java.util.List;

public record UserInfoResponse(
        String id,
        String name,
        String email,
        String role,
        List<Long> tagIds,
        String level
) {
}
