package com.lxp.content.resource.infrastructure.persistence.write.adapter;

import com.lxp.content.resource.domain.model.entity.Resource;
import com.lxp.content.resource.domain.model.vo.ResourceDate;
import com.lxp.content.resource.infrastructure.persistence.write.entity.ResourceJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ResourceWriteMapper {

    public ResourceJpaEntity toEntity(Resource r) {
        ResourceDate rd = r.resourceDate();
        return new ResourceJpaEntity(
            r.resourceId().asString(),
            r.storageKey(),
            r.ownerId(),
            r.fileStatus(),
            null, // contentType not tracked in domain; leave null or manage elsewhere
            r.sizeBytes(),
            r.etag(),
            rd != null ? rd.uploadedAt() : null,
            rd != null ? rd.attachedAt() : null,
            rd != null ? rd.deletedAt() : null
        );
    }

    public void updateEntity(ResourceJpaEntity e, Resource r) {
        // Only update mutable fields; identifiers remain unchanged
        e.setFileStatus(r.fileStatus());
        // contentType not set here, as domain holds only UploadType
        e.setSizeBytes(r.sizeBytes());
        e.setEtag(r.etag());
        var rd = r.resourceDate();
        if (rd != null) {
            e.setUploadedAt(rd.uploadedAt());
            e.setAttachedAt(rd.attachedAt());
            e.setDeletedAt(rd.deletedAt());
        }
        // ownerId might be considered immutable, but if business allows, uncomment below
        // e.setOwnerId(r.ownerId());
    }
}
