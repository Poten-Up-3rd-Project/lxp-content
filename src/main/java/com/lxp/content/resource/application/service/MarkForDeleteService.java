package com.lxp.content.resource.application.service;

import com.lxp.content.resource.application.port.provided.command.MarkForDeleteCommand;
import com.lxp.content.resource.application.port.provided.usecase.MarkForDeleteUseCase;
import com.lxp.content.resource.application.port.required.ResourcePort;
import com.lxp.content.resource.application.port.required.ResourceQueryPort;
import com.lxp.content.resource.domain.exception.ResourceNotFoundException;
import com.lxp.content.resource.domain.model.entity.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MarkForDeleteService implements MarkForDeleteUseCase {

    private final ResourceQueryPort resourceQuery;
    private final ResourcePort resourceWriter;

    @Override
    public void execute(MarkForDeleteCommand cmd) {
        Resource r = resourceQuery.findByStorageKey(cmd.key())
            .orElseThrow(() -> new ResourceNotFoundException("resource not found: " + cmd.key()));
        r.markForDelete();
        resourceWriter.save(r);
    }
}
