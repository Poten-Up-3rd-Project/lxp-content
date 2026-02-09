package com.lxp.content.resource.integration;

import com.lxp.content.resource.testsupport.DotenvSupport;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Smoke test that PUTs an image to Cloudflare R2 using AWS SDK v2,
 * without Spring/domain/DB. Loads credentials from .env in project root.
 * Required keys:
 * - R2_ACCOUNT_ID
 * - R2_ACCESS_KEY_ID
 * - R2_SECRET_ACCESS_KEY
 * - R2_BUCKET
 * Optional:
 * - R2_ENDPOINT (if omitted, built from R2_ACCOUNT_ID)
 */
@Disabled
class R2ImageSmokeIT {

    @Test
    void put_image_and_head_should_succeed() {
        Map<String, String> env = DotenvSupport.loadFromProjectRoot();
        String accountId = require(env, "R2_ACCOUNT_ID");
        String accessKey = require(env, "R2_ACCESS_KEY_ID");
        String secretKey = require(env, "R2_SECRET_ACCESS_KEY");
        String bucket = require(env, "R2_BUCKET");
        String endpoint = env.get("R2_ENDPOINT");
        if (endpoint == null || endpoint.isBlank()) {
            endpoint = String.format("https://%s.r2.cloudflarestorage.com", accountId);
        }

        S3Client s3 = S3Client.builder()
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)))
            .region(Region.of("auto"))
            .endpointOverride(URI.create(endpoint))
            .build();

        String key = String.format("images/smoke/%s.png", UUID.randomUUID());

        byte[] payload = "png-payload".getBytes(StandardCharsets.UTF_8);

        s3.putObject(
            PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("image/png")
                .build(),
            RequestBody.fromBytes(payload)
        );

        HeadObjectResponse head = s3.headObject(HeadObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build());

        assertNotNull(head);
        assertTrue(head.contentLength() > 0, "contentLength should be > 0");
        assertEquals("image/png", head.contentType());
    }

    private static String require(Map<String, String> m, String key) {
        String v = m.get(key);
        if (v == null || v.isBlank()) {
            throw new IllegalStateException(".env missing key: " + key);
        }
        return v;
    }
}
