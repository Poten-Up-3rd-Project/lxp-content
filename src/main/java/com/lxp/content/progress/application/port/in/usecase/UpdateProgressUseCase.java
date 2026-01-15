package com.lxp.content.progress.application.port.in.usecase;

import com.lxp.common.application.port.in.UseCase;
import com.lxp.content.progress.application.port.in.command.UpdateProgressCommand;

/**
 * 강좌 진행률 업데이트 유스 케이스
 */
public interface UpdateProgressUseCase extends UseCase<UpdateProgressCommand, Void> {

    @Override
    Void execute(UpdateProgressCommand command);

}
