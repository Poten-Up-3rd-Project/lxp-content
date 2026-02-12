package com.lxp.content.resource.infrastructure.r2.storage.fallback;

import com.lxp.content.resource.application.port.provided.result.PresignedUrlResult;
import com.lxp.content.resource.application.port.required.StorageObjectPort;
import com.lxp.content.resource.application.port.required.StoragePresignPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;
import java.time.Duration;
import java.util.Map;

@Configuration
public class StorageFallbackConfig {

    @Bean
    @ConditionalOnMissingBean(StorageObjectPort.class)
    public StorageObjectPort noopStorageObjectAdapter() {
        return new NoopStorageObjectAdapter();
    }

    @Bean
    @ConditionalOnMissingBean(StoragePresignPort.class)
    public StoragePresignPort noopStoragePresignPort() {
        return new StoragePresignPort() {
            @Override
            public PresignedUrlResult generateUploadUrl(String key, String contentType, Duration ttl) {
                // Return a predictable dummy URL and empty headers so services depending on it can proceed in tests
                return new PresignedUrlResult(key, "http://localhost/disabled-presign/" + key, "PUT", Map.of());
            }

            @Override
            public URL getPresignedUrl(String key) {
                return null;
            }
        };
    }
}
