package com.lxp.content.resource.application.port.required;

import com.lxp.content.resource.application.port.provided.result.PresignedUrlResult;

import java.time.Duration;

@FunctionalInterface
public interface StoragePresignPort {

    PresignedUrlResult generateUploadUrl(String key, String contentType, Duration ttl);

}
