package com.lxp.content.resource.infrastructure.persistence.write.repository;

import com.lxp.content.resource.infrastructure.persistence.write.entity.ResourceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResourceWriteRepository extends JpaRepository<ResourceJpaEntity, Long> {

    Optional<ResourceJpaEntity> findByUuid(String uuid);

    Optional<ResourceJpaEntity> findByStorageKey(String storageKey);
}
