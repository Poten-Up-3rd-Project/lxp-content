package com.lxp.content.resource.domain.model.entity;

import com.lxp.common.domain.event.AggregateRoot;
import com.lxp.content.resource.domain.exception.ResourceErrorCode;
import com.lxp.content.resource.domain.exception.ResourceException;
import com.lxp.content.resource.domain.model.vo.FileStatus;
import com.lxp.content.resource.domain.model.vo.ResourceDate;
import com.lxp.content.resource.domain.model.vo.ResourceId;
import com.lxp.content.resource.domain.model.vo.UploadType;

import java.util.Objects;

public class Resource extends AggregateRoot<ResourceId> {

    private final ResourceId resourceId;
    private final String storageKey;
    private final String ownerId;
    private FileStatus fileStatus;
    private UploadType uploadType;
    private Long sizeBytes;
    private String etag;
    private ResourceDate resourceDate;

    private Resource(
        ResourceId resourceId,
        String storageKey,
        String ownerId,
        FileStatus status,
        UploadType uploadType,
        Long sizeBytes,
        String etag,
        ResourceDate resourceDate
    ) {
        this.resourceId = Objects.requireNonNull(resourceId);
        this.storageKey = Objects.requireNonNull(storageKey);
        this.ownerId = Objects.requireNonNull(ownerId);
        this.fileStatus = Objects.requireNonNull(status);
        this.uploadType = uploadType;
        this.sizeBytes = sizeBytes;
        this.etag = etag;
        this.resourceDate = resourceDate;
    }

    public static Resource requested(String ownerId, String storageKey, UploadType uploadType) {
        return new Resource(
            ResourceId.create(),
            storageKey,
            ownerId,
            FileStatus.REQUESTED,
            uploadType,
            null,
            null,
            ResourceDate.created()
        );
    }

    public static Resource reconstruct(
        ResourceId resourceId,
        String storageKey,
        String ownerId,
        FileStatus status,
        UploadType uploadType,
        Long sizeBytes,
        String etag,
        ResourceDate resourceDate
    ) {
        return new Resource(resourceId, storageKey, ownerId, status, uploadType, sizeBytes, etag, resourceDate);
    }

    public void markUploaded(UploadType uploadType, long size, String etag) {
        ensureTransition(FileStatus.UPLOADED);
        this.fileStatus = FileStatus.UPLOADED;
        this.uploadType = uploadType;
        this.sizeBytes = size;
        this.etag = etag;
        this.resourceDate = this.resourceDate.withUploadedAt();
    }

    public void attach() {
        ensureTransition(FileStatus.ATTACHED);
        this.fileStatus = FileStatus.ATTACHED;
        this.resourceDate = this.resourceDate.withAttachedAt();
    }

    public void detach() {
        if (!this.fileStatus.isAttached()) throw new IllegalStateException("not attached");
        this.fileStatus = FileStatus.UPLOADED;
    }

    public void markForDelete() {
        ensureTransition(FileStatus.MARKED_FOR_DELETE);
        this.fileStatus = FileStatus.MARKED_FOR_DELETE;
    }

    public void markDeleted() {
        ensureTransition(FileStatus.DELETED);
        this.fileStatus = FileStatus.DELETED;
        this.resourceDate = this.resourceDate.withDeletedAt();
    }

    private void ensureTransition(FileStatus next) {
        if (!this.fileStatus.canTransitionTo(next)) {
            throw new ResourceException(ResourceErrorCode.INVALID_STATUS_TRANSITION, "illegal status transition: " + fileStatus + " -> " + next);
        }
    }

    public ResourceId resourceId() {
        return resourceId;
    }

    public String storageKey() {
        return storageKey;
    }

    public String ownerId() {
        return ownerId;
    }

    public FileStatus fileStatus() {
        return fileStatus;
    }

    public UploadType uploadType() {
        return uploadType;
    }

    public Long sizeBytes() {
        return sizeBytes;
    }

    public String etag() {
        return etag;
    }

    public ResourceDate resourceDate() {
        return resourceDate;
    }

    @Override
    public ResourceId getId() {
        return resourceId;
    }
}
