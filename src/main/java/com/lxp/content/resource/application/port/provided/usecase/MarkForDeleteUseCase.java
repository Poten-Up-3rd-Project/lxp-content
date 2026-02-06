package com.lxp.content.resource.application.port.provided.usecase;

import com.lxp.common.application.port.in.CommandUseCase;
import com.lxp.content.resource.application.port.provided.command.MarkForDeleteCommand;

@FunctionalInterface
public interface MarkForDeleteUseCase extends CommandUseCase<MarkForDeleteCommand> {}
