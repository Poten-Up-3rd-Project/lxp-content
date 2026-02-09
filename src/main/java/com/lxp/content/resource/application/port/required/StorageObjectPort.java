package com.lxp.content.resource.application.port.required;

public interface StorageObjectPort {

    HeadObjectResult head(String key);

    void delete(String key);

    record HeadObjectResult(boolean exists, long size, String etag, String contentType) {
    }
}
