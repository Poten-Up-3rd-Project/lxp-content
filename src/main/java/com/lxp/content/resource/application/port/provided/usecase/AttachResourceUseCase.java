package com.lxp.content.resource.application.port.provided.usecase;

import com.lxp.common.application.port.in.CommandUseCase;
import com.lxp.content.resource.application.port.provided.command.AttachResourceCommand;

@FunctionalInterface
public interface AttachResourceUseCase extends CommandUseCase<AttachResourceCommand> {}
