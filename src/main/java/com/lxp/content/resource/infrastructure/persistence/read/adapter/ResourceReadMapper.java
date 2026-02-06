package com.lxp.content.resource.infrastructure.persistence.read.adapter;

import com.lxp.content.resource.domain.model.entity.Resource;
import com.lxp.content.resource.domain.model.vo.ResourceDate;
import com.lxp.content.resource.domain.model.vo.ResourceId;
import com.lxp.content.resource.domain.model.vo.UploadType;
import com.lxp.content.resource.infrastructure.persistence.read.dto.ResourceInfoProjection;
import org.springframework.stereotype.Component;

@Component
public class ResourceReadMapper {

    public Resource toDomain(ResourceInfoProjection projection) {
        String key = projection.getStorageKey();
        UploadType type = inferUploadType(key, projection.getContentType());
        return Resource.reconstruct(
            ResourceId.of(projection.getUuid()),
            key,
            projection.getOwnerId(),
            projection.getFileStatus(),
            type,
            projection.getSizeBytes(),
            projection.getEtag(),
            ResourceDate.of(
                projection.getCreatedAt(),
                projection.getUploadedAt(),
                projection.getAttachedAt(),
                projection.getDeletedAt()
            )
        );
    }

    private UploadType inferUploadType(String storageKey, String contentType) {
        if (storageKey != null) {
            if (storageKey.startsWith("images/")) return UploadType.IMAGE;
            if (storageKey.startsWith("videos/")) return UploadType.VIDEO;
        }
        if (contentType != null) {
            try {
                return UploadType.fromContentType(contentType);
            } catch (Exception ignored) {}
        }
        // default fallback
        return UploadType.VIDEO;
    }
}
