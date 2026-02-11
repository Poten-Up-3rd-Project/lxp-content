package com.lxp.content.resource.application.service;

import com.lxp.content.resource.application.port.provided.command.ConfirmUploadCommand;
import com.lxp.content.resource.application.port.provided.usecase.ConfirmUploadUseCase;
import com.lxp.content.resource.application.port.required.ResourcePort;
import com.lxp.content.resource.application.port.required.ResourceQueryPort;
import com.lxp.content.resource.application.port.required.StorageObjectPort;
import com.lxp.content.resource.domain.exception.ResourceNotFoundException;
import com.lxp.content.resource.domain.exception.UnsupportedContentTypeException;
import com.lxp.content.resource.domain.model.entity.Resource;
import com.lxp.content.resource.domain.model.vo.UploadType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ConfirmUploadService implements ConfirmUploadUseCase {

    private final ResourceQueryPort resourceRepository;
    private final ResourcePort resourceWriter;
    private final StorageObjectPort storageObjectPort;

    @Override
    public void execute(ConfirmUploadCommand cmd) {
        Resource r = resourceRepository.findByStorageKey(cmd.key())
            .orElseThrow(() -> new ResourceNotFoundException("resource not found by key: " + cmd.key()));

        var head = storageObjectPort.head(cmd.key());
        if (!head.exists()) {
            throw new ResourceNotFoundException("object not found on storage: " + cmd.key());
        }

        UploadType type = r.uploadType();
        String actualContentType = head.contentType();
        if (actualContentType != null && !"application/octet-stream".equalsIgnoreCase(actualContentType)) {
            if (!type.supports(actualContentType)) {
                throw new UnsupportedContentTypeException(actualContentType);
            }
        }
        type.validateSize(head.size());

        r.markUploaded(type, head.size(), head.etag());
        resourceWriter.save(r);
    }
}
