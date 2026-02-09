package com.lxp.content.resource.infrastructure.web.dto;

import java.util.Map;

public record UploadUrlResponse(
    String key,
    String url,
    String method,
    Map<String, String> headers
) {
}
