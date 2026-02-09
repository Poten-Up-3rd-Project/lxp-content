package com.lxp.content.resource.infrastructure.persistence.read.dto;

import com.lxp.content.resource.domain.model.vo.FileStatus;

import java.time.Instant;

public interface ResourceInfoProjection {

    String getId();

    String getUuid();

    String getStorageKey();

    FileStatus getFileStatus();

    String getOwnerId();

    String getContentType(); // may be null; kept for backward-compat

    Long getSizeBytes();

    String getEtag();

    Instant getCreatedAt();

    Instant getUpdatedAt();

    Instant getUploadedAt();

    Instant getAttachedAt();

    Instant getDeletedAt();

}
