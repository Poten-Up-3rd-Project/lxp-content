package com.lxp.content.resource.infrastructure.persistence.read.repository;

import com.lxp.content.resource.infrastructure.persistence.read.dto.ResourceInfoProjection;
import com.lxp.content.resource.infrastructure.persistence.write.entity.ResourceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResourceReadRepository extends JpaRepository<ResourceJpaEntity, Long> {

    Optional<ResourceInfoProjection> findByStorageKey(String storageKey);
}
