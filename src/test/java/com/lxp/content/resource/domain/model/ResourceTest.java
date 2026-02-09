package com.lxp.content.resource.domain.model;

import com.lxp.content.resource.domain.exception.FileSizeExceededException;
import com.lxp.content.resource.domain.exception.ResourceException;
import com.lxp.content.resource.domain.model.entity.Resource;
import com.lxp.content.resource.domain.model.vo.FileStatus;
import com.lxp.content.resource.domain.model.vo.ResourceDate;
import com.lxp.content.resource.domain.model.vo.UploadType;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResourceTest {

    @Test
    void requested_to_uploaded_success() {
        Resource r = Resource.requested("u1", "videos/u1/k1", UploadType.VIDEO);
        assertThat(r.fileStatus()).isEqualTo(FileStatus.REQUESTED);

        r.markUploaded(UploadType.VIDEO, 1_000_000L, "etag");

        assertThat(r.fileStatus()).isEqualTo(FileStatus.UPLOADED);
        assertThat(r.sizeBytes()).isEqualTo(1_000_000L);
        assertThat(r.etag()).isEqualTo("etag");
        assertThat(r.resourceDate().uploadedAt()).isNotNull();
    }

    @Test
    void attach_then_detach_success() {
        Resource r = Resource.requested("u1", "videos/u1/k1", UploadType.VIDEO);
        r.markUploaded(UploadType.VIDEO, 10, "e");

        r.attach();
        assertThat(r.fileStatus()).isEqualTo(FileStatus.ATTACHED);
        assertThat(r.resourceDate().attachedAt()).isNotNull();

        r.detach();
        assertThat(r.fileStatus()).isEqualTo(FileStatus.UPLOADED);
    }

    @Test
    void invalid_transition_attach_from_requested_throws() {
        Resource r = Resource.requested("u1", "videos/u1/k1", UploadType.VIDEO);
        assertThrows(ResourceException.class, r::attach);
    }

    @Test
    void mark_for_delete_and_delete_success() {
        Resource r = Resource.requested("u1", "videos/u1/k1", UploadType.VIDEO);

        r.markForDelete();
        assertThat(r.fileStatus()).isEqualTo(FileStatus.MARKED_FOR_DELETE);

        r.markDeleted();
        assertThat(r.fileStatus()).isEqualTo(FileStatus.DELETED);
        assertThat(r.resourceDate().deletedAt()).isNotNull();
    }

    @Test
    void upload_type_size_validation() {
        assertThrows(FileSizeExceededException.class,
            () -> UploadType.IMAGE.validateSize(6L * 1024 * 1024)); // > 5MB

        UploadType.VIDEO.validateSize(100L * 1024 * 1024); // ok
    }

    @Test
    void resource_date_garbage_logic() {
        var now = Instant.now();
        var created = now.minus(Duration.ofMinutes(20));
        var uploaded = now.minus(Duration.ofMinutes(20));

        var dRequested = ResourceDate.of(created, null, null, null);
        assertThat(dRequested.isGarbage(FileStatus.REQUESTED, now,
            Duration.ofMinutes(10), Duration.ofMinutes(10))).isTrue();

        var dUploaded = ResourceDate.of(created, uploaded, null, null);
        assertThat(dUploaded.isGarbage(FileStatus.UPLOADED, now,
            Duration.ofMinutes(10), Duration.ofMinutes(10))).isTrue();
    }
}
