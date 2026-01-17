package com.lxp.content.progress.application.port.in.usecase;

import com.lxp.common.application.port.in.UseCase;
import com.lxp.content.progress.application.port.in.command.CreateProgressCommand;

/**
 * 강좌 진행률 생성 유스케이스
 */
public interface CreateCourseProgressUseCase extends UseCase<CreateProgressCommand, Void> {
}
