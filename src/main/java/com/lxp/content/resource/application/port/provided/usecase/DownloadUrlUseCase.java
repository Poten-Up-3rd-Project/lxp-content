package com.lxp.content.resource.application.port.provided.usecase;

import com.lxp.common.application.port.in.CommandWithResultUseCase;
import com.lxp.content.resource.application.port.provided.command.DownloadUrlCommand;

import java.net.URL;

@FunctionalInterface
public interface DownloadUrlUseCase extends CommandWithResultUseCase<DownloadUrlCommand, URL> {
}
