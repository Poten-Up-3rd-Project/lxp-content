package com.lxp.content.resource.application.service;

import com.lxp.content.resource.application.port.provided.query.GenerateUploadUrlQuery;
import com.lxp.content.resource.application.port.provided.result.PresignedUrlResult;
import com.lxp.content.resource.application.port.provided.usecase.GenerateUploadUrlUseCase;
import com.lxp.content.resource.application.port.required.ResourcePort;
import com.lxp.content.resource.application.port.required.StoragePresignPort;
import com.lxp.content.resource.domain.model.entity.Resource;
import com.lxp.content.resource.domain.model.vo.UploadType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResourceUploadQueryService implements GenerateUploadUrlUseCase {

    private final StoragePresignPort storagePresignPort;
    private final ResourcePort resourceRepository;

    @Value("${r2.default-ttl-seconds:900}")
    private long defaultTtlSeconds;

    @Override
    public PresignedUrlResult execute(GenerateUploadUrlQuery query) {
        UploadType type = query.uploadType();
        type.isNotSupportedThenThrow(query.contentType());
        String key = String.format("%s/%s", type.keyPrefix(), UUID.randomUUID());

        Resource resource = Resource.requested(query.userId(), key, type);
        resourceRepository.save(resource);

        Duration ttl = Duration.ofSeconds(defaultTtlSeconds);
        return storagePresignPort.generateUploadUrl(key, query.contentType(), ttl);
    }
}
