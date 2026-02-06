package com.lxp.content.resource.application.port.provided.usecase;

import com.lxp.common.application.port.in.QueryUseCase;
import com.lxp.content.resource.application.port.provided.query.GenerateUploadUrlQuery;
import com.lxp.content.resource.application.port.provided.result.PresignedUrlResult;

@FunctionalInterface
public interface GenerateUploadUrlUseCase extends QueryUseCase<GenerateUploadUrlQuery, PresignedUrlResult> {
}
