package com.lxp.content.resource.infrastructure.persistence.write.adapter;

import com.lxp.content.resource.application.port.required.ResourcePort;
import com.lxp.content.resource.domain.model.entity.Resource;
import com.lxp.content.resource.infrastructure.persistence.write.entity.ResourceJpaEntity;
import com.lxp.content.resource.infrastructure.persistence.write.repository.ResourceWriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ResourceWriteAdapter implements ResourcePort {

    private final ResourceWriteRepository resourceWriteRepository;
    private final ResourceWriteMapper resourceWriteMapper;

    @Override
    public void save(Resource resource) {
        String uuid = resource.resourceId().asString();

        Optional<ResourceJpaEntity> existing = resourceWriteRepository.findByUuid(uuid)
            .or(() -> resourceWriteRepository.findByStorageKey(resource.storageKey()));

        if (existing.isPresent()) {
            ResourceJpaEntity entity = existing.get();
            resourceWriteMapper.updateEntity(entity, resource);
            resourceWriteRepository.save(entity);
        } else {
            ResourceJpaEntity entity = resourceWriteMapper.toEntity(resource);
            resourceWriteRepository.save(entity);
        }
    }
}
