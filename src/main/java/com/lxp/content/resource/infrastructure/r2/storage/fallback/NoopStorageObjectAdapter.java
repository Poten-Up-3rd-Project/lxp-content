package com.lxp.content.resource.infrastructure.r2.storage.fallback;

import com.lxp.content.resource.application.port.required.StorageObjectPort;

public class NoopStorageObjectAdapter implements StorageObjectPort {
    @Override
    public HeadObjectResult head(String key) {
        return new HeadObjectResult(false, 0, null, null);
    }

    @Override
    public void delete(String key) {
        // no-op
    }
}
