package com.lxp.content.resource.application.port.required;

import com.lxp.content.resource.application.port.provided.result.PresignedUrlResult;

import java.net.URL;
import java.time.Duration;

public interface StoragePresignPort {

    PresignedUrlResult generateUploadUrl(String key, String contentType, Duration ttl);

    URL getPresignedUrl(String key);

}
