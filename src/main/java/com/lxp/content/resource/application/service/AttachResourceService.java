package com.lxp.content.resource.application.service;

import com.lxp.content.resource.application.port.provided.command.AttachResourceCommand;
import com.lxp.content.resource.application.port.provided.usecase.AttachResourceUseCase;
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
public class AttachResourceService implements AttachResourceUseCase {

    private final ResourceQueryPort resourceQuery;
    private final ResourcePort resourceWriter;

    @Override
    public void execute(AttachResourceCommand cmd) {
        Resource r = resourceQuery.findByStorageKey(cmd.key())
            .orElseThrow(() -> new ResourceNotFoundException("resource not found: " + cmd.key()));
        r.attach();
        resourceWriter.save(r);
    }
}
