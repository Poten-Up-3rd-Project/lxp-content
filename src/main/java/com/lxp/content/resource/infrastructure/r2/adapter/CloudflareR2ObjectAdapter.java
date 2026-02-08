package com.lxp.content.resource.infrastructure.r2.adapter;

import com.lxp.content.resource.application.port.required.StorageObjectPort;
import com.lxp.content.resource.infrastructure.r2.config.R2Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

@Component
@ConditionalOnProperty(name = "r2.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class CloudflareR2ObjectAdapter implements StorageObjectPort {

    private final S3Client s3Client;
    private final R2Properties props;

    @Override
    public HeadObjectResult head(String key) {
        try {
            var resp = s3Client.headObject(HeadObjectRequest.builder()
                .bucket(props.getBucket())
                .key(key)
                .build());
            long size = resp.contentLength();
            String etag = resp.eTag();
            String contentType = resp.contentType();
            return new HeadObjectResult(true, size, etag, contentType);
        } catch (NoSuchKeyException e) {
            return new HeadObjectResult(false, 0, null, null);
        }
    }

    @Override
    public void delete(String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
            .bucket(props.getBucket())
            .key(key)
            .build());
    }
}
