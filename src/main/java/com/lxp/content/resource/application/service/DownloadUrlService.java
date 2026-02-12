package com.lxp.content.resource.application.service;

import com.lxp.content.resource.application.port.provided.command.DownloadUrlCommand;
import com.lxp.content.resource.application.port.provided.usecase.DownloadUrlUseCase;
import com.lxp.content.resource.application.port.required.StoragePresignPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URL;

@Component
@RequiredArgsConstructor
public class DownloadUrlService implements DownloadUrlUseCase {

    private final StoragePresignPort storagePresignPort;

    @Override
    public URL execute(DownloadUrlCommand command) {
        return storagePresignPort.getPresignedUrl(command.key());
    }
}
