package com.lxp.content.resource.application.service;

import com.lxp.content.resource.application.port.provided.command.DeleteResourceCommand;
import com.lxp.content.resource.application.port.provided.usecase.DeleteResourceUseCase;
import com.lxp.content.resource.application.port.required.ResourcePort;
import com.lxp.content.resource.application.port.required.ResourceQueryPort;
import com.lxp.content.resource.application.port.required.StorageObjectPort;
import com.lxp.content.resource.domain.exception.ResourceNotFoundException;
import com.lxp.content.resource.domain.model.entity.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteResourceService implements DeleteResourceUseCase {

    private final ResourceQueryPort resourceQuery;
    private final ResourcePort resourceWriter;
    private final StorageObjectPort storage;

    @Override
    public void execute(DeleteResourceCommand cmd) {
        Resource r = resourceQuery.findByStorageKey(cmd.key())
            .orElseThrow(() -> new ResourceNotFoundException("resource not found: " + cmd.key()));
        storage.delete(cmd.key());
        r.markDeleted();
        resourceWriter.save(r);
    }
}
