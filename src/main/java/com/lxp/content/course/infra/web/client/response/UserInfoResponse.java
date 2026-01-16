package com.lxp.content.course.infra.web.client.response;

import java.time.LocalDateTime;

public record UserInfoResponse(
        String id,
        String name,
        String email,
        String role,
        String status,
        LocalDateTime deletedAt
) {
}

