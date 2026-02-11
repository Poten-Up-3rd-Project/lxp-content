package com.lxp.content.resource.integration;

import com.lxp.content.resource.application.port.provided.command.ConfirmUploadCommand;
import com.lxp.content.resource.application.port.required.ResourcePort;
import com.lxp.content.resource.application.port.required.ResourceQueryPort;
import com.lxp.content.resource.application.service.ConfirmUploadService;
import com.lxp.content.resource.domain.model.entity.Resource;
import com.lxp.content.resource.domain.model.vo.UploadType;
import com.lxp.content.resource.testsupport.DotenvSupport;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Spring integration test using an external MinIO instance configured via .env (project root).
 * Required .env keys:
 * - MINIO_ENDPOINT (e.g., http://localhost:9000)
 * - MINIO_ACCESS_KEY_ID
 * - MINIO_SECRET_ACCESS_KEY
 * - MINIO_BUCKET (should exist or be creatable)
 * Optional:
 * - MINIO_REGION (default: us-east-1)
 * - MINIO_IT_ENABLED=true (if set to false, test is skipped)
 */
@TestPropertySource(properties = "r2.enabled=true")
@SpringBootTest
class ConfirmUploadServiceMinioDotenvIT {

    private static final Map<String, String> ENV = DotenvSupport.loadFromProjectRoot();

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        String endpoint = ENV.get("MINIO_ENDPOINT");
        String access = ENV.get("MINIO_ACCESS_KEY_ID");
        String secret = ENV.get("MINIO_SECRET_ACCESS_KEY");
        String bucket = ENV.get("MINIO_BUCKET");
        String region = ENV.getOrDefault("MINIO_REGION", "us-east-1");

        if (endpoint != null) r.add("r2.endpoint", () -> endpoint);
        if (access != null) r.add("r2.access-key-id", () -> access);
        if (secret != null) r.add("r2.secret-access-key", () -> secret);
        if (bucket != null) r.add("r2.bucket", () -> bucket);
        r.add("r2.region", () -> region);
        r.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        r.add("r2.path-style", () -> true);
    }

    @Autowired
    S3Client s3;
    @Autowired
    ConfirmUploadService confirm;
    @Autowired
    ResourcePort resourceWriter;
    @Autowired
    ResourceQueryPort resourceQuery;

    @BeforeAll
    static void guard() {
        // Skip if .env missing or explicitly disabled
        Assumptions.assumeTrue(!ENV.isEmpty(), ".env not found at project root; skipping MinIO dotenv IT");
        String enabled = ENV.getOrDefault("MINIO_IT_ENABLED", "true");
        Assumptions.assumeTrue(Boolean.parseBoolean(enabled), "MINIO_IT_ENABLED is false; skipping");
    }

    @Test
    void confirm_upload_with_external_minio_from_dotenv() {
        String bucket = ENV.get("MINIO_BUCKET");
        Assumptions.assumeTrue(bucket != null && !bucket.isBlank(), "MINIO_BUCKET missing; skipping");

        try {
            s3.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
        } catch (Exception ignored) {
        }

        String key = "videos/minio-it-object";
        s3.putObject(
            PutObjectRequest.builder().bucket(bucket).key(key).contentType("video/mp4").build(),
            software.amazon.awssdk.core.sync.RequestBody.fromString("hello", StandardCharsets.UTF_8)
        );

        Resource r = Resource.requested("u-dotenv", key, UploadType.VIDEO);
        resourceWriter.save(r);

        confirm.execute(new ConfirmUploadCommand(key));

        Optional<Resource> found = resourceQuery.findByStorageKey(key);
        assertThat(found).isPresent();
        assertThat(found.get().fileStatus().isUploaded()).isTrue();
        assertThat(found.get().sizeBytes()).isNotNull();
        assertThat(found.get().etag()).isNotNull();
    }
}
