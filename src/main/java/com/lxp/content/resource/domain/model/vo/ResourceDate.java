package com.lxp.content.resource.domain.model.vo;

import com.lxp.content.resource.domain.support.ResourceGuard;

import java.time.Duration;
import java.time.Instant;

public record ResourceDate(
    Instant createdAt,
    Instant uploadedAt,
    Instant attachedAt,
    Instant deletedAt
) {

    public ResourceDate {
        ResourceGuard.requireNonNull(createdAt, "생성일은 null일 수 없습니다.");
    }

    public static ResourceDate of(Instant createdAt,
                                  Instant uploadedAt,
                                  Instant attachedAt,
                                  Instant deletedAt) {
        return new ResourceDate(createdAt, uploadedAt, attachedAt, deletedAt);
    }

    public static ResourceDate created() {
        return new ResourceDate(Instant.now(), null, null, null);
    }

    public ResourceDate withUploadedAt() {
        return new ResourceDate(this.createdAt, Instant.now(), this.attachedAt, this.deletedAt);
    }

    public ResourceDate withAttachedAt() {
        return new ResourceDate(this.createdAt, this.uploadedAt, Instant.now(), this.deletedAt);
    }

    public ResourceDate withDeletedAt() {
        return new ResourceDate(this.createdAt, this.uploadedAt, this.attachedAt, Instant.now());
    }

    public boolean isGarbage(
        FileStatus status,
        Instant now,
        Duration requestedTtl,
        Duration uploadedTtl
    ) {
        return switch (status) {
            case REQUESTED -> createdAt != null &&
                createdAt.isBefore(now.minus(requestedTtl));

            case UPLOADED -> uploadedAt != null &&
                uploadedAt.isBefore(now.minus(uploadedTtl));

            default -> false;
        };
    }
}
