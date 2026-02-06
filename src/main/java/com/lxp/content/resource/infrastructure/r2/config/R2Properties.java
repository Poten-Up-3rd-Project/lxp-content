package com.lxp.content.resource.infrastructure.r2.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "storage.r2")
public class R2Properties {
    /** Account ID used in the endpoint, e.g. https://<accountId>.r2.cloudflarestorage.com */
    private String accountId;
    /** R2 access key (S3 compatible) */
    private String accessKeyId;
    /** R2 secret key (S3 compatible) */
    private String secretAccessKey;
    /** Bucket name to store uploads */
    private String bucket;
    /** Optional custom endpoint. If empty, built from accountId. */
    private String endpoint;
    /** Region hint for SDK. R2 commonly uses 'auto'. */
    private String region = "auto";
    /** Default TTL seconds for presigned URL (fallback when not provided elsewhere). */
    private long defaultTtlSeconds = 900;
    /** Enable path-style addressing (useful for MinIO/local S3). */
    private boolean pathStyle = false;
}
