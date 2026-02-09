package com.lxp.content.resource.application.port.required;

import com.lxp.content.resource.domain.model.entity.Resource;

import java.util.Optional;

public interface ResourceQueryPort {

    Optional<Resource> findByStorageKey(String storageKey);

}
