package com.lxp.content.resource.infrastructure.r2.adapter;

import com.lxp.content.resource.application.port.provided.result.PresignedUrlResult;
import com.lxp.content.resource.application.port.required.StoragePresignPort;
import com.lxp.content.resource.infrastructure.r2.config.R2Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "r2.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class CloudflareR2PresignAdapter implements StoragePresignPort {

    private final S3Presigner presigner;
    private final R2Properties props;

    @Override
    public PresignedUrlResult generateUploadUrl(String key, String contentType, Duration ttl) {
        PresignedPutObjectRequest presigned = generatePresignedRequest(key, contentType, ttl);

        Map<String, String> headers = new HashMap<>();
        SdkHttpRequest httpReq = presigned.httpRequest();
        httpReq.headers().forEach((h, v) -> headers.put(h, String.join(",", v)));

        return new PresignedUrlResult(
            key,
            presigned.url().toString(),
            httpReq.method().name(),
            headers
        );
    }

    private PresignedPutObjectRequest generatePresignedRequest(String key, String contentType, Duration ttl) {
        PutObjectRequest putReq = PutObjectRequest.builder()
            .bucket(props.getBucket())
            .key(key)
            .contentType(contentType)
            .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(ttl)
            .putObjectRequest(putReq)
            .build();

        return presigner.presignPutObject(presignRequest);
    }
}
