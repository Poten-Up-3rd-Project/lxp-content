package com.lxp.content.resource.application.port.provided.query;

import com.lxp.content.resource.domain.model.vo.UploadType;

public record GenerateUploadUrlQuery(
    String userId,
    UploadType uploadType,
    String contentType
) {
}
