package com.lxp.content.resource.infrastructure.r2.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

import static java.util.Objects.isNull;

@Configuration
@ConditionalOnProperty(
    name = "r2.enabled",
    havingValue = "true",
    matchIfMissing = true
)
@EnableConfigurationProperties(R2Properties.class)
public class R2Config {

    private static S3Configuration s3Cfg(R2Properties props) {
        return S3Configuration.builder()
            .pathStyleAccessEnabled(props.isPathStyle())
            .build();
    }

    @Bean
    public S3Presigner s3Presigner(R2Properties props) {
        String endpoint = props.getEndpoint();
        if (isNull(endpoint) || endpoint.isBlank()) {
            endpoint = String.format("https://%s.r2.cloudflarestorage.com", props.getAccountId());
        }
        return S3Presigner.builder()
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(props.getAccessKeyId(), props.getSecretAccessKey())
            ))
            .region(Region.of(props.getRegion()))
            .endpointOverride(URI.create(endpoint))
            .serviceConfiguration(s3Cfg(props))
            .build();
    }

    @Bean
    public S3Client s3Client(R2Properties props) {
        String endpoint = props.getEndpoint();
        if (isNull(endpoint) || endpoint.isBlank()) {
            endpoint = String.format("https://%s.r2.cloudflarestorage.com", props.getAccountId());
        }
        return S3Client.builder()
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(props.getAccessKeyId(), props.getSecretAccessKey())
            ))
            .region(Region.of(props.getRegion()))
            .endpointOverride(URI.create(endpoint))
            .serviceConfiguration(s3Cfg(props))
            .build();
    }
}
