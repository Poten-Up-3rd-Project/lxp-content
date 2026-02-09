package com.lxp.content.resource.infrastructure.persistence.read.adapter;

import com.lxp.content.resource.application.port.required.ResourceQueryPort;
import com.lxp.content.resource.domain.model.entity.Resource;
import com.lxp.content.resource.infrastructure.persistence.read.repository.ResourceReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ResourceReadAdapter implements ResourceQueryPort {

    private final ResourceReadRepository resourceReadRepository;
    private final ResourceReadMapper resourceReadMapper;

    @Override
    public Optional<Resource> findByStorageKey(String storageKey) {
        return resourceReadRepository.findByStorageKey(storageKey).map(resourceReadMapper::toDomain);
    }
}
