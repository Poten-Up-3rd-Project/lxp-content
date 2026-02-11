package com.lxp.content.resource.infrastructure.persistence.write.entity;

import com.lxp.common.infrastructure.persistence.BaseVersionedJpaEntity;
import com.lxp.content.resource.domain.model.vo.FileStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "resource", indexes = {
    @Index(name = "idx_resource_uuid", columnList = "uuid"),
    @Index(name = "idx_resource_storage_key", columnList = "storage_key")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ResourceJpaEntity extends BaseVersionedJpaEntity {

    @Column(nullable = false, unique = true)
    private String uuid;

    @Column(name = "storage_key", nullable = false, unique = true)
    private String storageKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileStatus fileStatus;

    @Column(name = "owner_id")
    private String ownerId;

    private String contentType;

    private Long sizeBytes;

    private String etag;

    @Column(name = "uploaded_at")
    private Instant uploadedAt;

    @Column(name = "attached_at")
    private Instant attachedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public ResourceJpaEntity(String uuid, String storageKey, String ownerId, FileStatus fileStatus,
                             String contentType, Long sizeBytes, String etag,
                             Instant uploadedAt, Instant attachedAt, Instant deletedAt) {
        this.uuid = uuid;
        this.storageKey = storageKey;
        this.ownerId = ownerId;
        this.fileStatus = fileStatus;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.etag = etag;
        this.uploadedAt = uploadedAt;
        this.attachedAt = attachedAt;
        this.deletedAt = deletedAt;
    }

    // Setters for mutable fields (for updates)
    public void setFileStatus(FileStatus fileStatus) {
        this.fileStatus = fileStatus;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public void setAttachedAt(Instant attachedAt) {
        this.attachedAt = attachedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }
}
