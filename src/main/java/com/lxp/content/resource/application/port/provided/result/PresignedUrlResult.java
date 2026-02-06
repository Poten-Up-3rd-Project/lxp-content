package com.lxp.content.resource.application.port.provided.result;

import java.util.Map;

public record PresignedUrlResult(
        String key,
        String url,
        String method,
        Map<String, String> headers
) {}
